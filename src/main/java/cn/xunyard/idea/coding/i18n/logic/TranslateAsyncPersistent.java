package cn.xunyard.idea.coding.i18n.logic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

/**
 * @author <a herf="mailto:wuqi@terminus.io">xunyard</a>
 * @date 2020-01-11
 */
public class TranslateAsyncPersistent {
    private static final Queue<ToPersistentPackage> persistentQueue = new ConcurrentLinkedQueue<>();
    private static Map<String, FileWriterPackage> fileWriterMap;

    static  {
        fileWriterMap = new HashMap<>();
        Executors.newSingleThreadExecutor().submit(new PersistentRunner());
    }

    private static class PersistentRunner implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                if (persistentQueue.isEmpty()) {
                    for (Map.Entry<String, FileWriterPackage> entry : fileWriterMap.entrySet()) {
                        if (entry.getValue().allowClose()) {
                            fileWriterMap.remove(entry.getKey());
                        }
                    }

                    Thread.sleep(100);
                } else {
                    try {
                        ToPersistentPackage toPersistentPackage;
                        while ((toPersistentPackage = persistentQueue.poll()) != null) {
                            String filepath = toPersistentPackage.getLanguageConfiguration().getFilepath();
                            FileWriterPackage writerPackage = fileWriterMap.get(filepath);
                            if (writerPackage == null) {
                                writerPackage = new FileWriterPackage(filepath);
                                fileWriterMap.put(filepath, writerPackage);
                            }

                            writerPackage.getFileWriter().write(String.format("%s=%s\n", toPersistentPackage.getErrorCode(), toPersistentPackage.getTranslate()));
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                    }
                }
            }
        }
    }

    private static class FileWriterPackage {
        private static final Integer FLUSH_UNUSED_COUNT = 10;
        private static final Integer MAX_UNUSED_COUNT = 100;
        private int unusedCount;
        private FileWriter fileWriter;

        public FileWriter getFileWriter() {
            unusedCount = 0;
            return fileWriter;
        }

        public FileWriterPackage(String filepath) throws IOException {
            fileWriter = new FileWriter(new File(filepath), true);
        }


        @SneakyThrows
        public boolean allowClose() {
            unusedCount++;

            if (unusedCount == FLUSH_UNUSED_COUNT) {
                fileWriter.flush();
            }

            if (unusedCount > MAX_UNUSED_COUNT) {
                fileWriter.close();
                return true;
            }

            return false;
        }
    }


    @Data
    @AllArgsConstructor
    private static class ToPersistentPackage {
        private LanguageConfiguration languageConfiguration;
        private String errorCode;
        private String translate;
    }

    public static void submit(LanguageConfiguration configuration, String errorCode, String translate) {
        persistentQueue.add(new ToPersistentPackage(configuration, errorCode, translate));
    }
}

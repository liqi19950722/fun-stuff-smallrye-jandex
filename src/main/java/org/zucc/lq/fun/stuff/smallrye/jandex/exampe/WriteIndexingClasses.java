package org.zucc.lq.fun.stuff.smallrye.jandex.exampe;

import org.jboss.jandex.Index;
import org.jboss.jandex.IndexWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class WriteIndexingClasses {
    public static void main(String[] args) throws IOException {
        var indexer = Index.of(Map.class);
        var file = Paths.get("./tmp").toFile();
        if (!file.exists())
            file.mkdirs();
        try (FileOutputStream outputStream = new FileOutputStream("./tmp/index.idx")) {
            IndexWriter writer = new IndexWriter(outputStream);
            writer.write(indexer);
        }


    }
}

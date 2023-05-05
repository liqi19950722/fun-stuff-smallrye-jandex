package org.zucc.lq.fun.stuff.smallrye.jandex.exampe;

import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadIndexClasses {
    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream("./tmp/jakarta.inject-api-2.0.1-jar.idx")) {
            IndexReader reader = new IndexReader(inputStream);
            Index index = reader.read();

            index.getKnownClasses().forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

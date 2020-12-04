package com.lcjing.lucene.index;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.lcjing.lucene.ik.IKAnalyzer6x;

/**
 * 删除索引
 *
 * @author lcjing
 * @date 2020/12/4
 */
public class DeleteIndex {
    public static void main(String[] args) {
        // 删除title中含有“美国”的文档
        deleteDoc("title", "美国");
    }

    /**
     * 删除文档
     *
     * @param field 文档字段
     * @param key   关键字
     */
    public static void deleteDoc(String field, String key) {
        Analyzer analyzer = new IKAnalyzer6x();
        IndexWriterConfig icw = new IndexWriterConfig(analyzer);
        Path indexPath = Paths.get("indexdir");
        Directory directory;
        try {
            directory = FSDirectory.open(indexPath);
            IndexWriter indexWriter = new IndexWriter(directory, icw);
            indexWriter.deleteDocuments(new Term(field, key));
            indexWriter.commit();
            indexWriter.close();
            System.out.println("删除完成!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

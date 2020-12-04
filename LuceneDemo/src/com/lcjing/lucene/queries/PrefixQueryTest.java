package com.lcjing.lucene.queries;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Lucene 前缀搜索
 *
 * @author lcjing
 * @date 2020/12/4
 */
public class PrefixQueryTest {

    public static void main(String[] args) throws IOException {
        String field = "title";
        Path indexPath = Paths.get("indexdir");
        Directory dir = FSDirectory.open(indexPath);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 搜索以 "学" 开头的词项的文档
        Term term = new Term("title", "学");
        Query prefixQuery = new PrefixQuery(term);
        System.out.println("Query:" + prefixQuery);
        // 返回前10条
        TopDocs tds = searcher.search(prefixQuery, 10);
        for (ScoreDoc sd : tds.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID:" + sd.doc);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title:" + doc.get("title"));
            System.out.println("文档评分:" + sd.score);
        }
        dir.close();
        reader.close();
    }
}

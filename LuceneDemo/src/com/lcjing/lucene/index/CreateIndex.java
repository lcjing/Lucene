package com.lcjing.lucene.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.lcjing.lucene.ik.IKAnalyzer6x;

/**
 * 创建索引
 * <p>
 * Lucene 字段类型
 * 1. TextField: 把字段内容索引并词条化, 但是不保存词向量
 * 2. StringField: 对字段内容索引, 但并不词条化, 也不保存词向量
 * 3. IntPoint: 适合索引值为 int 类型的字段. IntPoint 是为了快速过滤的, 如果需要展示出来需要另存一个字段
 * 4. LongPoint
 * 5. FloatPoint
 * 6. DoublePoint
 * 7. SortedDocValuesField: 存储值为文本内容的 DocValue 字段, 适合索引字段值为文本内容并且需要按值进行排序的字段
 * 8. SortedSetDocValuesField: 存储多值域的 DocValues 字段, 适合索引字段值为文本内容并且需要按值进行分组、聚合等操作的字段
 * 9. NumericDocValuesField: 存储单个数值类型的 DocValues 字段, 主要包括(int、 long、 float、 double)
 * 10.SortedNumericDocValuesField: 存储数值类型的有序数组列表的 DocValues 字段
 * 11.StoredField: 适合索引只需要保存字段值不进行其他操作的字段
 * <p>
 * DocValues 其实是 Lucene 构建索引是额外建立一个有序的基于 document -> field/value 的映射列表. 它减轻了在排序和分组时对
 * 内存的依赖, 而且大大提升了这个过程的性能, 会耗费一定的磁盘空间
 * </p>
 *
 * @author lcjing
 * @date 2020/12/4
 */
public class CreateIndex {

    public static void main(String[] args) {

        // 创建2个News对象
        News news1 = new News();
        news1.setId(1);
        news1.setTitle("安倍晋三本周会晤特朗普 将强调日本对美国益处");
        news1.setContent("日本首相安倍晋三计划2月10日在华盛顿与美国总统特朗普举行会晤时提出加大日本在美国投资的设想");
        news1.setReply(672);

        News news2 = new News();
        news2.setId(2);
        news2.setTitle("北大迎4380名新生 农村学生700多人近年最多");
        news2.setContent("昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多...");
        news2.setReply(995);

        News news3 = new News();
        news3.setId(3);
        news3.setTitle("特朗普宣誓(Donald Trump)就任美国第45任总统");
        news3.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。");
        news3.setReply(1872);

        // 开始时间
        Date start = new Date();
        System.out.println("**********开始创建索引**********");
        // 创建IK分词器
        Analyzer analyzer = new IKAnalyzer6x();
        IndexWriterConfig icw = new IndexWriterConfig(analyzer);
        /**
         * <p>
         *     setOpenMode() 设置索引的打开方式
         *     OpenMode.CREATE: 表示先清空索引再重新创建
         *     OpenMode.CREATE_OR_APPEND: 表示索引不存在会新建, 存在则附加
         * </p>
         */
        icw.setOpenMode(OpenMode.CREATE);
        Directory dir = null;
        IndexWriter inWriter = null;
        // 索引目录
        Path indexPath = Paths.get("indexdir");

        try {
            if (!Files.isReadable(indexPath)) {
                System.out.println("Document directory '" + indexPath.toAbsolutePath() + "' does not exist or is not readable, please check the path");
                System.exit(1);
            }
            dir = FSDirectory.open(indexPath);
            inWriter = new IndexWriter(dir, icw);

            /**
             * <p>
             *     FieldType 用于指定域的索引信息, 比如: 是否解析、是否存储、是否保存词项频率、位移信息等
             *     setIndexOptions() 用于设定域的索引选项:
             *     IndexOptions.DOCS: 只索引文档, 词项频率和位移信息不保存
             *     IndexOptions.DOCS_AND_FREQS:  只索引文档和词项频率, 位移信息不保存
             *     IndexOptions.DOCS_AND_FREQS_AND_POSITIONS: 索引文档、词项频率和位移信息
             *     IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS: 索引文档、词项频率、位移信息和偏移量
             *     IndexOptions.NONE: 不索引
             *
             * </p>
             */
            FieldType idType = new FieldType();
            idType.setIndexOptions(IndexOptions.DOCS);
            idType.setStored(true);

            FieldType titleType = new FieldType();
            titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            titleType.setStored(true);
            titleType.setTokenized(true);

            FieldType contentType = new FieldType();
            contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            // 设置为true存储字段值
            contentType.setStored(true);
            // 使用分词器对字段值进行词条化
            contentType.setTokenized(true);
            // 保存词向量
            contentType.setStoreTermVectors(true);
            // 保存词项在词向量中的位移信息
            contentType.setStoreTermVectorPositions(true);
            // 保存词项在词向量中的偏移信息
            contentType.setStoreTermVectorOffsets(true);

            Document doc1 = new Document();
            doc1.add(new Field("id", String.valueOf(news1.getId()), idType));
            doc1.add(new Field("title", news1.getTitle(), titleType));
            doc1.add(new Field("content", news1.getContent(), contentType));
            doc1.add(new IntPoint("reply", news1.getReply()));
            doc1.add(new StoredField("reply_display", news1.getReply()));

            Document doc2 = new Document();
            doc2.add(new Field("id", String.valueOf(news2.getId()), idType));
            doc2.add(new Field("title", news2.getTitle(), titleType));
            doc2.add(new Field("content", news2.getContent(), contentType));
            doc2.add(new IntPoint("reply", news2.getReply()));
            doc2.add(new StoredField("reply_display", news2.getReply()));

            Document doc3 = new Document();
            doc3.add(new Field("id", String.valueOf(news3.getId()), idType));
            doc3.add(new Field("title", news3.getTitle(), titleType));
            doc3.add(new Field("content", news3.getContent(), contentType));
            doc3.add(new IntPoint("reply", news3.getReply()));
            doc3.add(new StoredField("reply_display", news3.getReply()));

            inWriter.addDocument(doc1);
            inWriter.addDocument(doc2);
            inWriter.addDocument(doc3);
            inWriter.commit();
            inWriter.close();
            dir.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.println("索引文档用时:" + (end.getTime() - start.getTime()) + " milliseconds");
        System.out.println("**********索引创建完成**********");
    }
}

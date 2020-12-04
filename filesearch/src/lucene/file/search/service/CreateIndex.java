package lucene.file.search.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import lucene.file.search.model.FileModel;

/**
 * 提取所有的文档内容, 创建索引, 索引文件保存在 indexdir 目录
 *
 * @author lcjing
 * @date 2020/12/3
 */
public class CreateIndex {

    public static void main(String[] args) throws Exception {
        // IK分词器对象
        Analyzer analyzer = new IKAnalyzer6x();
        IndexWriterConfig icw = new IndexWriterConfig(analyzer);
        icw.setOpenMode(OpenMode.CREATE);
        Directory dir = null;
        IndexWriter inWriter = null;
        Path indexPath = Paths.get("WebRoot/indexdir");

        FieldType fileType = new FieldType();
        fileType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fileType.setStored(true);
        fileType.setTokenized(true);
        fileType.setStoreTermVectors(true);
        fileType.setStoreTermVectorPositions(true);
        fileType.setStoreTermVectorOffsets(true);
        // 开始时间
        Date start = new Date();
        if (!Files.isReadable(indexPath)) {
            System.out.println(indexPath.toAbsolutePath() + "不存在或者不可读，请检查!");
            System.exit(1);
        }
        dir = FSDirectory.open(indexPath);
        inWriter = new IndexWriter(dir, icw);
        ArrayList<FileModel> fileList = (ArrayList<FileModel>) extractFile();
        // 遍历fileList,建立索引
        for (FileModel f : fileList) {
            Document doc = new Document();
            doc.add(new Field("title", f.getTitle(), fileType));
            doc.add(new Field("content", f.getContent(), fileType));
            inWriter.addDocument(doc);
        }

        inWriter.commit();
        inWriter.close();
        dir.close();

        // 结束时间
        Date end = new Date();
        // 打印索引耗时
        System.out.println("索引文档完成,共耗时:" + (end.getTime() - start.getTime()) + "毫秒.");
    }

    /**
     * 列出WebRoot/files目录下的索所有文件
     *
     * @return FileModel类型的列表
     */
    public static List<FileModel> extractFile() throws Exception {
        ArrayList<FileModel> list = new ArrayList<>();
        File fileDir = new File("WebRoot/files");
        if (!fileDir.exists()) {
            System.out.println("文件夹路径错误!");
        }
        File[] allFiles = fileDir.listFiles();

        for (File f : allFiles) {
            FileModel sf = new FileModel(f.getName(), parserFile(f));
            list.add(sf);
        }
        return list;
    }

    /**
     * 使用Tika提取文件内容
     *
     * @param file 文件对象
     * @return String格式的文档内容
     */
    public static String parserFile(File file) throws Exception {
        // 接收文档内容
        String fileContent = "";
        BodyContentHandler handler = new BodyContentHandler();
        // 自动解析器接口
        Parser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        FileInputStream inputStream;
        inputStream = new FileInputStream(file);
        ParseContext context = new ParseContext();
        parser.parse(inputStream, handler, metadata, context);
        fileContent = handler.toString();
        return fileContent;
    }
}

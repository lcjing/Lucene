package lucene.file.search.model;

/**
 * 文档模型类
 *
 * @author lcjing
 * @date 2020/12/3
 */
public class FileModel {
    /**
     * 文件名
     */
    private String title;

    /**
     * 文件内容
     */
    private String content;

    public FileModel() {
    }

    public FileModel(String title, String content) {
        this.title = title;
        this.content = content;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

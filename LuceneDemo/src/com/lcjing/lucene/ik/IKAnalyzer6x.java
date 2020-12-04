package com.lcjing.lucene.ik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * IK 分词器
 *
 * @author lcjing
 * @date 2020/12/4
 */
public class IKAnalyzer6x extends Analyzer {
    private boolean useSmart;

    /**
     * IK分词器Lucene Analyzer接口实现类; 默认细粒度切分算法
     */
    public IKAnalyzer6x() {
        this(false);
    }

    /**
     * IK分词器Lucene Analyzer接口实现类; 当为true时, 分词器进行智能切分
     *
     * @param useSmart true: 智能切分
     */
    public IKAnalyzer6x(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    public boolean useSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    /**
     * 重写最新版本的createComponents;重载Analyzer接口，构造分词组件
     *
     * @param fieldName 字段名
     * @return TokenStreamComponents
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer _IKTokenizer = new IKTokenizer6x(this.useSmart());
        return new TokenStreamComponents(_IKTokenizer);
    }
}
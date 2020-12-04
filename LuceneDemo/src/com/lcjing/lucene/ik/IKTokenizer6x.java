package com.lcjing.lucene.ik;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * IK 分词器
 *
 * @author lcjing
 * @date 2020/12/4
 */
public class IKTokenizer6x extends Tokenizer {
    /**
     * IK分词器实现
     */
    private IKSegmenter _IKImplement;
    /**
     * 词元文本属性
     */
    private final CharTermAttribute termAtt;
    /**
     * 词元位移属性
     */
    private final OffsetAttribute offsetAtt;
    /**
     * 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
     */
    private final TypeAttribute typeAtt;
    /**
     * 记录最后一个词元的结束位置
     */
    private int endPosition;

    /**
     * Lucene 6.x Tokenizer适配器类构造函数; 实现最新的Tokenizer接口
     *
     * @param useSmart true: 智能切分
     */
    public IKTokenizer6x(boolean useSmart) {
        super();
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        _IKImplement = new IKSegmenter(input, useSmart);
    }

    /**
     * 判断是否还有下一个词元
     *
     * @return true: 还有下一个词元  false: 词元输出完毕
     * @throws IOException IOException
     */
    @Override
    public boolean incrementToken() throws IOException {
        // 清除所有的词元属性
        clearAttributes();
        Lexeme nextLexeme = _IKImplement.next();
        if (nextLexeme != null) {
            // 将Lexeme转成Attributes
            // 设置词元文本
            termAtt.append(nextLexeme.getLexemeText());
            // 设置词元长度
            termAtt.setLength(nextLexeme.getLength());
            // 设置词元位移
            offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
            // 记录分词的最后位置
            endPosition = nextLexeme.getEndPosition();
            // 记录词元分类
            typeAtt.setType(nextLexeme.getLexemeText());
            // 返会true告知还有下个词元
            return true;
        }
        // 返会false告知词元输出完毕
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        _IKImplement.reset(input);
    }

    @Override
    public final void end() {
        int finalOffset = correctOffset(this.endPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }
}

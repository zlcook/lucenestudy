import org.apache.logging.log4j.core.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.Test;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by dell on 2017/6/9.
 */
public class LunceneTest {
    //解析文件中的文本内容
    @Test
    public void testParse() throws IOException, TikaException {
        String fileName = "E:\\IntelliJWorkspace\\test.txt";
        Tika tika = new Tika();
        File file = new File(fileName);
        System.out.println(file.exists());
        if( file.exists()) {
            String parseToString = tika.parseToString(file);
            System.out.println(fileName+"文件内容：" + parseToString);
        }else{
            System.out.println(fileName+"文件不存在");
        }
    }

    //创建索引
    @Test
    public void testLuncene() throws IOException {
        //创建目录类，path是索引库的位置
        String path = "E:\\indexs";
        FSDirectory directory = FSDirectory.open(new File(path));
        //创建分词器
        StandardAnalyzer analyzer = new StandardAnalyzer();
        //创建config
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
        //创建索引
        IndexWriter indexWriter = new IndexWriter(directory,conf);
        //创建文档对象，索引库中存在的索引都是以文档的形式存在的
        Document doc = new Document();
        //文档中需要设置field
        doc.add(new StringField("id","1", Field.Store.YES));
        doc.add(new TextField("title","lucene是一套搜索引擎API", Field.Store.YES));
        doc.add(new TextField("content","lucene，学的ddd好", Field.Store.YES));
        indexWriter.addDocument(doc);
        //提交索引
        indexWriter.commit();
        indexWriter.close();
    }

    //查询
    @Test
    public void seacherIndex() throws IOException, ParseException {
        //读取索引
        String path = "E:\\indexs";
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(new File(path)));
        //创建查询索引类IndexSearcher
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //查询关键字
        String searchName = "一";
        //创建查询解析器
        QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
        //创建查询解析器解析关键字
        Query query =  queryParser.parse(searchName);
        //查询并返回前10个文档的概要信息，还没有返回文档内容
        TopDocs topDocs = indexSearcher.search(query,10);
        //文档中的个数，也就是返回文档的个数
        int totalHits = topDocs.totalHits;
        //目前返回的只有一个文档，我们只手动设置一个文档
        System.out.println("返回文档个数："+totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for( ScoreDoc s : scoreDocs ){
          /*  //文档ID
            System.out.println("文档ID:"+s.doc);
            //文档打分
            System.out.println("文档打分:"+s.score);*/
            //查询文档内容,根据文档ID进行查询
            Document doc = indexSearcher.doc(s.doc);
            String id = doc.get("id");
            String title = doc.get("title");
            String content = doc.get("content");
            System.out.println(id+"||"+title+"||"+content);

        }
    }

    //从本地文件目录中创建索引
    @Test
    public void testCreateLunceneForLocalSource() throws Exception {
        //本地文件夹
        String resourceDir ="E:\\indexsource";
        List<Document> list = LuceneUnitls.file2Document(resourceDir);

        //创建目录类，path是索引库的位置
        String path = "E:\\indexs";
        File file = new File(path);
        if( !file.exists()){
            file.mkdirs();
        }
        FSDirectory directory = FSDirectory.open(file);
        //创建分词器
        StandardAnalyzer analyzer = new StandardAnalyzer();
        //创建config
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
        //创建索引
        IndexWriter indexWriter = new IndexWriter(directory,conf);
        //创建文档对象，索引库中存在的索引都是以文档的形式存在的
          for(Document doc : list){
              indexWriter.addDocument(doc);
          }
        //提交索引
        indexWriter.commit();
        indexWriter.close();
    }


    //查询
    @Test
    public void testSeacherIndex() throws IOException, ParseException {
        //读取索引
        String path = "E:\\indexs";
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(new File(path)));
        //创建查询索引类IndexSearcher
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //查询关键字
        String searchName = "Jtable控件.txt";
        //创建查询解析器
        QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
        //创建查询解析器解析关键字
        Query query = new TermQuery(new Term("fileName",searchName));// queryParser.parse(searchName);
        //查询并返回前10个文档的概要信息，还没有返回文档内容
        TopDocs topDocs = indexSearcher.search(query,10);
        //文档中的个数，也就是返回文档的个数
        int totalHits = topDocs.totalHits;
        //目前返回的只有一个文档，我们只手动设置一个文档
        System.out.println("返回文档个数："+totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for( ScoreDoc s : scoreDocs ){
            //查询文档内容,根据文档ID进行查询
            Document doc = indexSearcher.doc(s.doc);
            String title = doc.get("fileName");
            String fpath = doc.get("filePath");
            String content = doc.get("fileContent");
            System.out.println("文件名:"+title);
            System.out.println("文件内容:"+content);
            System.out.println("文件路径:"+fpath);

        }
    }
}

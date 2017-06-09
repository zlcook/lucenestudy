import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by dell on 2017/6/9.
 */
public class MainController {

    private static final String indexPath ="E:\\indexs";
    public static  void main(String[] args) {

        banner();
        Scanner scanner = new Scanner(System.in);
        String key =scanner.nextLine();
        while( !key.equals("q")){

            if( key.equals("1")){ //创建索引
                try {
                    createIndex();
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("创建索引失败:"+e.getMessage());
                }
            }else if( key.equals("2")){//搜索
                try {
                    searchKey();
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("搜索出现异常:"+e.getMessage());
                }
            }else if( key.equals(3)){

            }else{
                System.out.println("输入有误");
            }
            banner();
             key =scanner.nextLine();
        }
        System.out.print("****退出成功****");

    }

    private static void banner() {

        System.out.println("\n*********************************");
        System.out.println("1:添加新的搜索内容");
        System.out.println("2:搜索内容");
        System.out.println("q:退出");
        System.out.println("*********************************");
        System.out.print("请输入序号选择相应操作：");
    }


    private static void searchKey() throws IOException, ParseException {
        //读取索引
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
        //创建查询索引类IndexSearcher
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //查询关键字

        System.out.print("输入查询关键字：");
        Scanner scanner = new Scanner(System.in);
        String searchName  =scanner.nextLine();
        //String searchName = "Jtable控件.txt";
        //创建查询解析器
        QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
        //创建查询解析器解析关键字
        Query query = new TermQuery(new Term("fileContent",searchName));// queryParser.parse(searchName);
        //Query query =  queryParser.parse(searchName);
        //查询并返回前10个文档的概要信息，还没有返回文档内容
        TopDocs topDocs = indexSearcher.search(query,10);
        //文档中的个数，也就是返回文档的个数
        int totalHits = topDocs.totalHits;
        //目前返回的只有一个文档，我们只手动设置一个文档
        System.out.println("返回文档个数："+totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        int count = 0;
        for( ScoreDoc s : scoreDocs ){
            //查询文档内容,根据文档ID进行查询
            Document doc = indexSearcher.doc(s.doc);
            String title = doc.get("fileName");
            String fpath = doc.get("filePath");
            String content = doc.get("fileContent");
            System.out.println("****************第【"+ count++ +"】个搜索结果*********************");
            System.out.println("文件名:"+title);
            if(content!= null && content.length() > 20 ){
                System.out.println("文件内容:"+content.substring(0,20));
            }else{
                System.out.println("文件内容:"+content);
            }
            System.out.println("文件路径:"+fpath);

        }
    }

    private static void createIndex() throws Exception {
        System.out.print("输入索引源(E:\\indexsource)：");
        Scanner scanner = new Scanner(System.in);
        String resourceDir  =scanner.nextLine();
        //本地文件夹
       // String resourceDir ="E:\\indexsource";
        List<Document> list = LuceneUnitls.file2Document(resourceDir);
        //创建目录类，path是索引库的位置
        File file = new File(indexPath);
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
        System.out.println("创建索引完成");
    }
}

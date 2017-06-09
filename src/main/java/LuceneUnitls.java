import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.tika.Tika;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/6/9.
 */
public class LuceneUnitls {

    /**
     * 根据文件路径得到Document集合
     * @param searchPath
     * @return
     * @throws Exception
     */
    public static List<Document> file2Document(String searchPath) throws Exception{
        //需求，我们需要读取磁盘文件，对磁盘文件进行搜索
        //创建document的集合
        List<Document> list = new ArrayList<Document>();
        //获取文件
        File folder = new File(searchPath);
        if (!folder.isDirectory()) {
            return null;
        }
        // 获取目录 中的所有文件
        File[] files = folder.listFiles();
        for (File file : files) {
            //文件名称
            String fileName = file.getName();
            System.out.println(fileName);
            String path = file.getPath();
            //使用Tika来读取任意文件
            Tika tika = new Tika();
            String fileContent = tika.parseToString(file);
            //创建文档
            Document doc = new Document();
            //创建各各Field域
            //文件名
            Field field_fileName = new TextField ("fileName", fileName, Field.Store.YES);
            //文件路径
            Field field_filePath = new TextField ("filePath", path, Field.Store.YES);
            //文件内容，由于读取文件内容很大，所以我们不保存
            Field field_fileContent = new TextField("fileContent", fileContent, Field.Store.YES);
            //将解析文件放入document
            doc.add(field_fileName);
            doc.add(field_filePath);
            doc.add(field_fileContent);
            list.add(doc);
        }
        return list;

    }

}

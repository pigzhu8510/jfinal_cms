package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.jflyfox.util.StrUtils;


/**
 * 映射数据库，自动生成Entity
 * @author WWF
 */
public class CreateJfinalEntityUtil {
	
	private static Connection conn = null;
	private static final String URL;
	private static final String JDBC_DRIVER;
	private static final String USER_NAME;
	private static final String PASSWORD;
	private static final String PACKAGENAME;
	static {
		URL = "jdbc:mysql://localhost:3306/jfinal_cms";
		JDBC_DRIVER = "com.mysql.jdbc.Driver";
		USER_NAME = "root";
		PASSWORD = "root";
		PACKAGENAME = "test";
	}
	
	/**
	 * 获得连接
	 * @return
	 */
	public static Connection getConnection() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 关闭数据库
	 */
	public static void closeConnection(){
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成字段静态声明
	 * @param connection
	 * @param tableName
	 * @return
	 */
	private static String CreateEntityString(String tableName){
		String result  = "package "+PACKAGENAME+";\n\n";
		result += "import com.jfinal.plugin.activerecord.Model; \n\n";
		result += "public class "+StrUtils.toLowerCaseFirst(tableName)+" extends Model<"+StrUtils.toLowerCaseFirst(tableName)+">{\n\n";
		result += "    private static final long serialVersionUID = 1L;\n";
		result += "    public static final "+StrUtils.toLowerCaseFirst(tableName)+" dao = new "+StrUtils.toLowerCaseFirst(tableName)+"();\n\n";
		result += "    /**表名**/ \n" ;
		result += "    public static String TABLE = \""+tableName+"\";" ;
		String sql = "select column_name from information_schema.columns where table_name = '"+tableName+"';";
		try {
			Statement statement = conn.createStatement();
			ResultSet resultSet =  statement.executeQuery(sql);
			while (resultSet.next()) {
				if (resultSet.getString(1)!=null&&!resultSet.getString(1).isEmpty()) {
					String string = resultSet.getString(1);
					String row = "    public static final String "+ string.toUpperCase(Locale.CHINA) +" = \""+string+"\";";
					String note = "    /****/";
					result += "\n"+note + "\n" +row;
				}
			}
			resultSet.close();
			statement.close();
			result+="\n }";
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获得数据库的所有表名
	 * @return
	 */
	public static List<String> getAllTables(){
		String sql = "select relname as TABLE_NAME from pg_class c " +
				"where  relkind = 'r' and relname not like 'pg_%' and relname not like 'sql_%' order by relname";
		try {
			List<String> result = new ArrayList<String>();
			Statement statement = conn.createStatement();
			ResultSet resultSet =  statement.executeQuery(sql);
			while (resultSet.next()) {
				if (resultSet.getString(1)!=null&&!resultSet.getString(1).isEmpty()){
					result.add(resultSet.getString(1));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * 生成Entity
	 */
	public static void CreateEntity(){
		try {
			getConnection();
			List<String> tables = getAllTables();
			for (int i = 0; i < tables.size(); i++) {
				File createFolder = new File(System.getProperty("user.dir")+"/src/"+PACKAGENAME.replace(".", "/"));
				//路径不存在，生成文件夹
				if (!createFolder.exists()) {
					createFolder.mkdirs();
				}
				String entityString = CreateEntityString(tables.get(i).trim());
				File entity = new File(createFolder.getAbsolutePath()+"/"+StrUtils.toLowerCaseFirst(tables.get(i))+".java");
				if (!entity.exists())
				{
					//写入文件 
					BufferedWriter out = new BufferedWriter(new FileWriter(entity, true));
					out.write(entityString);
		            out.close();
		            out = null;
		            entity = null;
				}
			}
			closeConnection();
			System.out.println("生成成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		CreateEntity();
	}

	

}
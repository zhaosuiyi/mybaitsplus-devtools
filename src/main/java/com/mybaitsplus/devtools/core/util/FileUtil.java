package com.mybaitsplus.devtools.core.util;

import java.io.*;

/**
 * 文件工具类
 */
public class FileUtil {
	
	public static  byte[] getByte(String filePath){
		byte[] file_buff = null;
		if (filePath != null && !"".equals(filePath.trim())) {
			InputStream is = null;
			FileInputStream fileInputStream = null;
			try {
				File file = new File(filePath);
				fileInputStream = new FileInputStream(file);
				if (fileInputStream != null) {
					int len = fileInputStream.available();
					file_buff = new byte[len];
					fileInputStream.read(file_buff);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return file_buff ;
	}
	
	public static  byte[] getByte(InputStream is){
		byte[] file_buff = null;
		if (is!=null) {
			try {
					int len = is.available();
					file_buff = new byte[len];
					is.read(file_buff);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
			}
		}
		return file_buff ;
	}
	
	/**
	 * 从字节数组中获取字节长度
	 */
	public static long getLenFromByte(byte[] b) {
		InputStream in = null;
		in = new ByteArrayInputStream(b);
		int len = 0;
		try {
			len = in.available();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return len;
	}
	
	/** 
     * 方法二：
     * 根据byte数组，生成文件 
     */  
    public  static void writeFile(byte[] bfile, String filePath,String fileName) {  
        BufferedOutputStream bos = null;  

        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            } 
            file = new File(filePath+"\\"+fileName); 
           /* 使用以下2行代码时，不追加方式*/
            /*bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bfile); */
            
            /* 使用以下3行代码时，追加方式*/
            bos = new BufferedOutputStream(new FileOutputStream(file, true));
            bos.write(bfile); 
            bos.write("\r\n".getBytes()); 
            
            
            bos.flush();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            
        }  
    }
	
}

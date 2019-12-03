package com.mybaitsplus.devtools.core.util;

import com.mybaitsplus.devtools.core.exception.BusinessException;
import com.mybaitsplus.devtools.core.exception.ExcelException;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author     : WH
 * @group      : tgb8
 * @Date       : 2014-1-2 下午9:13:21
 * @Comments   : 导入导出Excel工具类
 * @Version    : 1.0.0
 */

public class ExcelUtil  {
	public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();
    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，可自定义工作表大小）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * 如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
     * 如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
     * fieldMap.put("college.collegeName","学院名称")
     * @param sheetName 工作表的名称
     * @param sheetSize 每个工作表中记录的最大个数
     * @param out       导出流
     * @throws ExcelException
     */
    public static <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String, String> fieldMap,
            String sheetName,
            int sheetSize,
            OutputStream out
            ) throws ExcelException {


        if(list.size()==0 || list==null){
            throw new ExcelException("数据源中没有任何数据");
        }

        if(sheetSize>65535 || sheetSize<1){
            sheetSize=65535;
        }

        //创建工作簿并发送到OutputStream指定的地方
        WritableWorkbook wwb;
        try {
            wwb = Workbook.createWorkbook(out);

            //因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
            //所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
            //1.计算一共有多少个工作表
            double sheetNum=Math.ceil(list.size()/new Integer(sheetSize).doubleValue());

            //2.创建相应的工作表，并向其中填充数据
            for(int i=0; i<sheetNum; i++){
                //如果只有一个工作表的情况
                if(1==sheetNum){
                    WritableSheet sheet=wwb.createSheet(sheetName, i);
                    fillSheet(sheet, list, fieldMap, 0, list.size()-1);

                //有多个工作表的情况
                }else{
                    WritableSheet sheet=wwb.createSheet(sheetName+(i+1), i);

                    //获取开始索引和结束索引
                    int firstIndex=i*sheetSize;
                    int lastIndex=(i+1)*sheetSize-1>list.size()-1 ? list.size()-1 : (i+1)*sheetSize-1;
                    //填充工作表
                    fillSheet(sheet, list, fieldMap, firstIndex, lastIndex);
                }
            }

            wwb.write();
            wwb.close();

        }catch (Exception e) {
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

            //否则将其它异常包装成ExcelException再抛出
            }else{
                throw new ExcelException("导出Excel失败");
            }
        }

    }

    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，工作表大小为2003支持的最大值）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param out       导出流
     * @throws ExcelException
     */
    public static  <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String, String> fieldMap,
            String sheetName,
            OutputStream out
            ) throws ExcelException{

        listToExcel(list, fieldMap, sheetName, 65535, out);

    }


    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（导出到浏览器，可以自定义工作表的大小）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param sheetSize    每个工作表中记录的最大个数
     * @param response  使用response可以导出到浏览器
     * @throws ExcelException
     */
    public static  <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String, String> fieldMap,
            String sheetName,
            int sheetSize,
            HttpServletResponse response,
            String fileName
            ) throws ExcelException{

        //设置默认文件名为当前时间：年月日时分秒
        //String fileName=new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();
    	String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();
    	fileName = fileName + dateName;
    	try {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
            //fileName = URLEncoder.encode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //设置response头信息
        response.reset();
        response.setContentType("application/vnd.ms-excel");        //改成输出excel文件
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");

        //创建工作簿并发送到浏览器
        try {

            OutputStream out=response.getOutputStream();
            listToExcel(list, fieldMap, sheetName, sheetSize,out );

        } catch (Exception e) {
            e.printStackTrace();

            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

            //否则将其它异常包装成ExcelException再抛出
            }else{
                throw new ExcelException("导出Excel失败");
            }
        }
    }


    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（导出到浏览器，工作表的大小是2003支持的最大值）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param response  使用response可以导出到浏览器
     * @param fileName 导出文件名
     * @throws ExcelException
     */
    public static <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String,String> fieldMap,
            String sheetName,
            HttpServletResponse response,
            String fileName
            ) throws ExcelException{

        listToExcel(list, fieldMap, sheetName, 65535, response, fileName);
    }

    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（导出到浏览器，工作表的大小是2003支持的最大值）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param response  使用response可以导出到浏览器
     * @param fileName 导出文件名
     * @throws ExcelException
     */
    public static <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String,String> fieldMap,
            HttpServletResponse response,
            String fileName
    ) throws ExcelException{
        listToExcel(list, fieldMap, "sheet1", 65535, response, fileName);
    }

    /**
     * @MethodName          : excelToList
     * @Description             : 将Excel转化为List
     * @param in                    ：承载着Excel的输入流
     * @param entityClass       ：List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap          ：Excel中的中文列头和类的英文属性的对应关系Map
     * @param uniqueFields  ：指定业务主键组合（即复合主键），这些列的组合不能重复
     * @param fileType  : 文件类型
     * @return                      ：List
     * @throws ExcelException
     */
    @SuppressWarnings("resource")
	public static <T>  List<T>  excelToList(
            InputStream in,
            String sheetName,
            Class<T> entityClass,
            LinkedHashMap<String, String> fieldMap,
            String[] uniqueFields,
            String  fileType,
            int titleRow
            ) throws ExcelException{

        //定义要返回的list
        List<T> resultList=new ArrayList<T>();
        try {             	
        	if(fileType.equals("xls")) {
        		//根据Excel数据源创建WorkBook
        		Workbook wb = Workbook.getWorkbook(in); 
                 //获取工作表
                 Sheet sheet=wb.getSheet(sheetName);

                 //获取工作表的有效行数
                 int realRows=0;
                 for(int i=0;i<sheet.getRows();i++){

                     int nullCols=0;
                     for(int j=0;j<sheet.getColumns();j++){
                         Cell currentCell=sheet.getCell(j,i);
                         if(currentCell==null || "".equals(currentCell.getContents().toString())){
                             nullCols++;
                         }
                     }

                     if(nullCols==sheet.getColumns()){
                         break;
                     }else{
                         realRows++;
                     }
                 }


                 //如果Excel中没有数据则提示错误
                 if(realRows<=1){
                     throw new ExcelException("Excel文件中没有任何数据");
                 }


                 Cell[] firstRow=sheet.getRow(0);

                 String[] excelFieldNames=new String[firstRow.length];

                 //获取Excel中的列名
                 for(int i=0;i<firstRow.length;i++){
                     excelFieldNames[i]=firstRow[i].getContents().toString().trim();
                 }

                 //判断需要的字段在Excel中是否都存在
                 boolean isExist=true;
                 List<String> excelFieldList=Arrays.asList(excelFieldNames);
                 for(String cnName : fieldMap.keySet()){
                     if(!excelFieldList.contains(cnName)){
                         isExist=false;
                         break;
                     }
                 }

                 //如果有列名不存在，则抛出异常，提示错误
                 if(!isExist){
                     throw new ExcelException("Excel中缺少必要的字段，或字段名称有误");
                 }


                 //将列名和列号放入Map中,这样通过列名就可以拿到列号
                 LinkedHashMap<String, Integer> colMap=new LinkedHashMap<String, Integer>();
                 for(int i=0;i<excelFieldNames.length;i++){
                     colMap.put(excelFieldNames[i], firstRow[i].getColumn());
                 } 



                 //判断是否有重复行
                 //1.获取uniqueFields指定的列
                 Cell[][] uniqueCells=new Cell[uniqueFields.length][];
                 for(int i=0;i<uniqueFields.length;i++){
                     int col=colMap.get(uniqueFields[i]);
                     uniqueCells[i]=sheet.getColumn(col);
                 }

                 //2.从指定列中寻找重复行
                 for(int i=1;i<realRows;i++){
                     int nullCols=0;
                     for(int j=0;j<uniqueFields.length;j++){
                         String currentContent=uniqueCells[j][i].getContents();
                         Cell sameCell=sheet.findCell(currentContent, 
                                 uniqueCells[j][i].getColumn(),
                                 uniqueCells[j][i].getRow()+1, 
                                 uniqueCells[j][i].getColumn(), 
                                 uniqueCells[j][realRows-1].getRow(), 
                                 true);
                         if(sameCell!=null){
                             nullCols++;
                         }
                     }

                     if(nullCols==uniqueFields.length){
                         throw new ExcelException("Excel中有重复行，请检查");
                     }
                 }

                 //将sheet转换为list
                 for(int i=1;i<realRows;i++){
                     //新建要转换的对象
                     T entity=entityClass.newInstance();

                     //给对象中的字段赋值
                     for(Entry<String, String> entry : fieldMap.entrySet()){
                         //获取中文字段名
                         String cnNormalName=entry.getKey();
                         //获取英文字段名
                         String enNormalName=entry.getValue();
                         //根据中文字段名获取列号
                         int col=colMap.get(cnNormalName);

                         //获取当前单元格中的内容
                         String content=sheet.getCell(col, i).getContents().toString().trim();

                         //给对象赋值
                         setFieldValueByName(enNormalName, content, entity);
                     }

                     resultList.add(entity);
                 }
        	} else {
        		 XSSFWorkbook xwb = new XSSFWorkbook(in);
        		 XSSFSheet xsheet = xwb.getSheetAt(0);       
        		 
        		 int realRows = xsheet.getPhysicalNumberOfRows(); //行数
        		 XSSFRow rowOne = xsheet.getRow(titleRow); //获取第一行数据
                 int cellnum = rowOne.getLastCellNum(); //第一行有多少列
                 String[] excelFieldNames=new String[cellnum];
                 //获取Excel中的列名
                 for( int columns=0;columns < cellnum;columns++){//读取第一列

                     String cellvalue="";
                     if(isMergedRegion(xsheet,titleRow,columns)){
                          cellvalue = getMergedRegionValueAndInfo(xsheet, titleRow, columns);
                     }else{
                         XSSFCell  cell = rowOne.getCell(columns);
                         cellvalue = cell.getStringCellValue().trim(); // 获取第一列的值
                     }
                     System.out.println(cellvalue);
                	 excelFieldNames[columns] = cellvalue;
                 }
                 //判断需要的字段在Excel中是否都存在
                 boolean isExist=true;
                 List<String> excelFieldList=Arrays.asList(excelFieldNames);
                 for(String cnName : fieldMap.keySet()){
                     if(!excelFieldList.contains(cnName)){
                         isExist=false;
                         break;
                     }
                 }
                 //如果有列名不存在，则抛出异常，提示错误
                 if(!isExist){
                     throw new ExcelException("Excel中缺少必要的字段，或字段名称有误");
                 }                 
               //将列名和列号放入Map中,这样通过列名就可以拿到列号
                 LinkedHashMap<String, Integer> colMap=new LinkedHashMap<String, Integer>();
                 for(int i=0;i<excelFieldNames.length;i++){
                     colMap.put(excelFieldNames[i], rowOne.getCell(i).getColumnIndex());
                 } 

                 /*//判断是否有重复数据
                 int firstRowNum = xsheet.getFirstRowNum() + 1; //第一行不计入从第二行开始
                 int lastRowNum = realRows - 1;  
                 for (int j = firstRowNum; j <= lastRowNum; j++) {  
                	 XSSFRow row = xsheet.getRow(j); 
                	 Map<Integer,String> map=new HashMap<Integer,String>();
                	 for (int n = 0; n < cellnum; n++){
                         row.getCell(n).setCellType(CellType.STRING);
                		 String value = row.getCell(n).getStringCellValue().trim(); //获取相应行相应列的值
                		 map.put(n, value);
                	 }
                	 //取得第j行    
                	 Map<Integer,String> tmap=new HashMap<Integer,String>();
                	 int m = j+1;
                	 for (int l = m ; l<=lastRowNum; l++){
                		 XSSFRow nextRow = xsheet.getRow(l);
                		 for (int u = 0; u < cellnum; u++){
                             nextRow.getCell(u).setCellType(CellType.STRING);
                			 String nextvalue = nextRow.getCell(u).getStringCellValue().trim(); //获取相应行相应列的值
                    		 tmap.put(u, nextvalue);
                    	 }
            	        String tmp1;  
            	        String tmp2;
            	        boolean b = false;
            	        for(Entry<Integer, String> entry: map.entrySet()){ //判断是否 有重复的数据
            	            if(tmap.containsKey(entry.getKey())){  
            	                tmp1=entry.getValue();  //map的值
            	                tmp2=tmap.get(entry.getKey());  
            	                  
            	                if(null!=tmp1 && null!=tmp1){   //都不为null  
            	                    if(tmp1.equals(tmp2)){  
            	                        b=true; 
            	                    }else{  
            	                        b=false; 
            	                        break;
            	                    }  
            	                } 
            	            }else{  
            	                b=false;  
            	                break;  
            	            }  
            	        } 
            	        if(b==true){
            	        	throw new ExcelException("Excel中有重复行，请检查");
            	        }               		                 		 
                	 }                      
                 }*/
                 //将sheet转换为list
                 for(int i=(titleRow+1);i<realRows;i++){
                     //新建要转换的对象
                     T entity=entityClass.newInstance();

                     //给对象中的字段赋值
                     for(Entry<String, String> entry : fieldMap.entrySet()){
                         //获取中文字段名
                         String cnNormalName=entry.getKey();
                         //获取英文字段名
                         String enNormalName=entry.getValue();
                         //根据中文字段名获取列号
                         int col=colMap.get(cnNormalName);
                         //获取当前单元格中的内容
                         xsheet.getRow(i).getCell(col).setCellType(CellType.STRING);
                         String content=xsheet.getRow(i).getCell(col).getStringCellValue().trim();
                         //给对象赋值
                         setFieldValueByName(enNormalName, content, entity);
                     }
                     resultList.add(entity);
                 }
                 
        	}
        } catch(Exception e){
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

            //否则将其它异常包装成ExcelException再抛出
            }else{
                e.printStackTrace();
                throw new ExcelException("导入Excel失败");
            }
        }
        return resultList;
    }





    /*<-------------------------辅助的私有方法----------------------------------------------->*/
    /**
     * @MethodName  : getFieldValueByName
     * @Description : 根据字段名获取字段值
     * @param fieldName 字段名
     * @param o 对象
     * @return  字段值
     */
    private static  Object getFieldValueByName(String fieldName, Object o) throws Exception{

        Object value=null;
        if(o instanceof  Map){
            return ((Map)o).get(fieldName);
        }
        Field field=getFieldByName(fieldName, o.getClass());

        if(field !=null){
            field.setAccessible(true);
            value=field.get(o);
        }else{
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }

        return value;
    }

    /**
     * @MethodName  : getFieldByName
     * @Description : 根据字段名获取字段
     * @param fieldName 字段名
     * @param clazz 包含该字段的类
     * @return 字段
     */
    private static Field getFieldByName(String fieldName, Class<?>  clazz){
        //拿到本类的所有字段
        Field[] selfFields=clazz.getDeclaredFields();

        //如果本类中存在该字段，则返回
        for(Field field : selfFields){
            if(field.getName().equals(fieldName)){
                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz=clazz.getSuperclass();
        if(superClazz!=null  &&  superClazz !=Object.class){
            return getFieldByName(fieldName, superClazz);
        }

        //如果本类和父类都没有，则返回空
        return null;
    }


    /**
     * @MethodName  : getFieldValueByNameSequence
     * @Description : 
     * 根据带路径或不带路径的属性名获取属性值
     * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
     * 
     * @param fieldNameSequence  带路径的属性名或简单属性名
     * @param o 对象
     * @return  属性值
     * @throws Exception
     */
    private static  Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception{

        Object value=null;

        //将fieldNameSequence进行拆分
        String[] attributes=fieldNameSequence.split("\\.");
        if(attributes.length==1){
            value=getFieldValueByName(fieldNameSequence, o);
        }else{
            //根据属性名获取属性对象
            Object fieldObj=getFieldValueByName(attributes[0], o);
            String subFieldNameSequence=fieldNameSequence.substring(fieldNameSequence.indexOf(".")+1);
            value=getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value; 

    } 


    /**
     * @MethodName  : setFieldValueByName
     * @Description : 根据字段名给对象的字段赋值
     * @param fieldName  字段名
     * @param fieldValue    字段值
     * @param o 对象
     */
    private static void setFieldValueByName(String fieldName,Object fieldValue,Object o) throws Exception{

            Field field=getFieldByName(fieldName, o.getClass());
            if(field!=null){
                field.setAccessible(true);
                //获取字段类型
                Class<?> fieldType = field.getType();  

                //根据字段类型给字段赋值
                if (String.class == fieldType) {  
                    field.set(o, String.valueOf(fieldValue));  
                } else if ((Integer.TYPE == fieldType)  
                        || (Integer.class == fieldType)) {  
                    field.set(o, Integer.parseInt(fieldValue.toString()));  
                } else if ((Long.TYPE == fieldType)  
                        || (Long.class == fieldType)) {  
                    field.set(o, Long.valueOf(fieldValue.toString()));  
                } else if ((Float.TYPE == fieldType)  
                        || (Float.class == fieldType)) {  
                    field.set(o, Float.valueOf(fieldValue.toString()));  
                } else if ((Short.TYPE == fieldType)  
                        || (Short.class == fieldType)) {  
                    field.set(o, Short.valueOf(fieldValue.toString()));  
                } else if ((Double.TYPE == fieldType)  
                        || (Double.class == fieldType)) {  
                    field.set(o, Double.valueOf(fieldValue.toString()));  
                } else if (Character.TYPE == fieldType) {  
                    if ((fieldValue!= null) && (fieldValue.toString().length() > 0)) {  
                        field.set(o, Character  
                                .valueOf(fieldValue.toString().charAt(0)));  
                    }  
                }else if(Date.class==fieldType){
                    field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
                }else if(BigDecimal.class== fieldType){
                    if(StringUtils.isEmpty(fieldValue.toString())){
                        fieldValue="0";
                    }
                    field.set(o, new BigDecimal(fieldValue.toString()));
                } else{
                    field.set(o, fieldValue);
                }
            }else{
                throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
            }
    }


    /**
     * @MethodName  : setColumnAutoSize
     * @Description : 设置工作表自动列宽和首行加粗
     * @param ws
     */
    private static void setColumnAutoSize(WritableSheet ws,int extraWith){
        //获取本列的最宽单元格的宽度
        for(int i=0;i<ws.getColumns();i++){
            int colWith=0;
            for(int j=0;j<ws.getRows();j++){
                String content=ws.getCell(i,j).getContents().toString();
                int cellWith=content.length();
                if(colWith<cellWith){
                    colWith=cellWith;
                }
            }
            //设置单元格的宽度为最宽宽度+额外宽度
            ws.setColumnView(i, colWith+extraWith);
        }

    }

    /**
     * @MethodName  : fillSheet
     * @Description : 向工作表中填充数据
     * @param sheet     工作表 
     * @param list  数据源
     * @param fieldMap 中英文字段对应关系的Map
     * @param firstIndex    开始索引
     * @param lastIndex 结束索引
     */
    private static <T> void fillSheet(
            WritableSheet sheet,
            List<T> list,
            LinkedHashMap<String,String> fieldMap,
            int firstIndex,
            int lastIndex
            )throws Exception{

        //定义存放英文字段名和中文字段名的数组
        String[] enFields=new String[fieldMap.size()];
        String[] cnFields=new String[fieldMap.size()];

        //填充数组
        int count=0;
        for(Entry<String,String> entry:fieldMap.entrySet()){
            enFields[count]=entry.getKey();
            cnFields[count]=entry.getValue();
            count++;
        }
        //填充表头
        for(int i=0;i<cnFields.length;i++){
            Label label=new Label(i,0,cnFields[i]);
            sheet.addCell(label);
        }

        //填充内容
        int rowNo=1;
        for(int index=firstIndex;index<=lastIndex;index++){
            //获取单个对象
            T item=list.get(index);
            for(int i=0;i<enFields.length;i++){
                Object objValue=getFieldValueByNameSequence(enFields[i], item);
                String fieldValue=objValue==null ? "" : objValue.toString();
                Label label =new Label(i,rowNo,fieldValue);
                sheet.addCell(label);
            }

            rowNo++;
        }

        //设置自动列宽
        setColumnAutoSize(sheet, 5);
    } 
    /**
     * 
    * @Title: getFileByFile
    * @Description: [getFileByFile,获取文件类型,包括图片,若格式不是已配置的,则返回null]
    * @param file
    * @return String    返回类型
    * @throws
    * @author zhangch  
    * @date 2018年1月18日 下午4:08:27
     */
    public final  String getFileByFile(File file) {    
       String filetype = null;    
       byte[] b = new byte[50];    
       try    
       {    
           InputStream is = new FileInputStream(file);    
           is.read(b);    
           filetype = getFileTypeByStream(b);    
           is.close();    
       }    
       catch (FileNotFoundException e)    
       {    
           e.printStackTrace();    
       }    
       catch (IOException e)    
       {    
           e.printStackTrace();    
       }    
       return filetype;    
   } 

    public final  String getFileTypeByStream(byte[] b) {    
       String filetypeHex = String.valueOf(getFileHexString(b));    
       Iterator<Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();    
       while (entryiterator.hasNext()) {    
           Entry<String,String> entry =  entryiterator.next();    
           String fileTypeHexValue = entry.getValue();    
           if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {    
               return entry.getKey();    
           }    
       }    
       return null;    
   }    
    public final  String getFileHexString(byte[] b) {    
        StringBuilder stringBuilder = new StringBuilder();    
        if (b == null || b.length <= 0)    
        {    
            return null;    
        }    
        for (int i = 0; i < b.length; i++)    
        {    
            int v = b[i] & 0xFF;    
            String hv = Integer.toHexString(v);    
            if (hv.length() < 2)    
            {    
                stringBuilder.append(0);    
            }    
            stringBuilder.append(hv);    
        }    
        return stringBuilder.toString();    
    } 
    /**
     * 
    * @Title: inputStream2String
    * @Description: 数据流转String
    * @param is
    * @return
    * @throws IOException String    返回类型
    * @throws
    * @author zhangch  
    * @date 2018年1月18日 下午4:15:18
     */
    public static String inputStream2String(InputStream is) throws IOException{
    	 BufferedReader in = new BufferedReader(new InputStreamReader(is));
    	 StringBuffer buffer = new StringBuffer();
    	 String line = "";
    	 while ((line = in.readLine()) != null){
    	  buffer.append(line);
    	 }
    	 return buffer.toString();
    }

    public static <T> void listToExcelTemplate(
            InputStream is,
            int rows,
            List<T> list ,
            LinkedHashMap<String, String> fieldMap,
            OutputStream out
    ) throws ExcelException {

        if(list.size()==0 || list==null){
            throw new ExcelException("数据源中没有任何数据");
        }
        //定义存放英文字段名和中文字段名的数组
        String[] enFields=new String[fieldMap.size()];
        String[] cnFields=new String[fieldMap.size()];
        try {

            //填充数组
            int count=0;
            for(Entry<String,String> entry:fieldMap.entrySet()){
                enFields[count]=entry.getKey();
                cnFields[count]=entry.getValue();
                count++;
            }

            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);

            for (int index = 0; index < list.size(); index++) {
                T item=list.get(index);
                XSSFRow row = sheet.createRow(index + rows);
                for(int i=0;i<enFields.length;i++){
                    Object objValue=getFieldValueByNameSequence(enFields[i], item);
                    String fieldValue=objValue==null ? "" : objValue.toString();
                    row.createCell(i).setCellValue(fieldValue);
                }
            }
            wb.write(out);
            out.flush();
            out.close();
        }catch (Exception e) {
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

                //否则将其它异常包装成ExcelException再抛出
            }else{
                throw new ExcelException("导出Excel失败");
            }
        }
    }

    /**
     * 判断合并了行
     * @param sheet
     * @param row  行
     * @param column  列
     * @return
     */
    private static boolean isMergedRow(XSSFSheet sheet,int row ,int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();  // 0 0   3 1
            if (row == firstRow && row == lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**  行合并
     * 判断单元格向行方向合并
     * 判断指定的单元格是否是合并单元格
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRegion(XSSFSheet sheet,int row ,int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public static String getCellValue(XSSFCell cell){
        return  cell.getStringCellValue().trim(); // 获取第一列的值
    }

    /**
     * 获取合并单元格的值
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValueAndInfo(XSSFSheet sheet ,int row , int column){
        String MergedVal="";
        String MergedInfo="";
        int sheetMergeCount = sheet.getNumMergedRegions();
        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                   // Row fRow = sheet.getRow(firstRow);
                    XSSFRow fRow = sheet.getRow(firstRow); //获取第一行数据
                    XSSFCell fCell = fRow.getCell(firstColumn);
                    MergedVal=getCellValue(fCell);
                    MergedInfo=String.valueOf(firstColumn)+","+String.valueOf(firstRow)+","+String.valueOf(lastColumn)+","+String.valueOf(lastRow);
                    return MergedVal;
                }
            }
        }
        return null ;
    }
    /**
     * 这是一个通用的方法，
     * @param sheetName
     *            表格sheet名
     * @param headers
     *            表格属性列名数组
     * @param headersField
     * 			  表格属性列名数组所对应的Map的Key值的集合
     * @param excelData
     *            需要显示的数据集合,集合中一定要放置符合Map风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param response
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    public  static void exportExcel(String sheetName,String[] headers,String[] headersField,List<Map<String, Object>> excelData
            ,OutputStream out){

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        int iMaxLines = 65534;
        // 生成一个表格
        int index = 0;
        int page =1;
        try {
            HSSFSheet sheet = workbook.createSheet(sheetName);
            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth((int) 15);
            // 产生表格标题行
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
            // 遍历集合数据，产生数据行
            Iterator<Map<String, Object>> it = excelData.iterator();

            while (it.hasNext()) {
                index++;
                row = sheet.createRow(index);
                Map<String, Object> t =  it.next();
                int m=0;
                for(short n = 0;n<headersField.length;n++) {
                    if(n==0)m=0;
                    HSSFCell cell = row.createCell(m);
                    m++;
                    Object value = t.get(headersField[n]);
                    String textValue = value==null?null:value.toString();
                    if (textValue != null) {
                        cell.setCellValue(textValue);
                    }
                }
            }
            String filename = sheetName + ".xls";
            try {
               workbook.write(out);// 将数据写出去
            } finally {
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        }
    }
}
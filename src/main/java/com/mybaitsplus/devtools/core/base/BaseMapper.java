/**
 * 
 */
package com.mybaitsplus.devtools.core.base;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.mapper.BaseMapper<T>  {

	List<Long> selectIdPage(@Param("cm") Map<String, Object> params);

	List<Long> selectIdPage(Pagination page, @Param("cm") Map<String, Object> params);

	List<T> selectObjsPage(@Param("cm") Map<String, Object> params);

	List<T> selectObjsPage(Pagination page, @Param("cm") Map<String, Object> params);

	void insertBatch(@Param("suffix") String suffix,@Param("list") List<T> list);

	boolean updateBatch(@Param("suffix") String suffix,@Param("list") List<T> list);
}

package com.collectorhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collectorhub.entity.ReleaseItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */
public interface ReleaseItemMapper extends BaseMapper<ReleaseItem> {

    List<ReleaseItem> queryReleaseItemOfCollectible(@Param("collectibleId") Long collectibleId);
}

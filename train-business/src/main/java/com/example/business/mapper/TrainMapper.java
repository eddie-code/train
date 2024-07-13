package com.example.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.business.domain.Train;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

@Repository
@CacheNamespace // MP二级缓存
public interface TrainMapper extends BaseMapper<Train> {
//    long countByExample(TrainExample example);
//
//    int deleteByExample(TrainExample example);
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(Train record);
//
//    int insertSelective(Train record);
//
//    List<Train> selectByExample(TrainExample example);
//
//    Train selectByPrimaryKey(Long id);
//
//    int updateByExampleSelective(@Param("record") Train record, @Param("example") TrainExample example);
//
//    int updateByExample(@Param("record") Train record, @Param("example") TrainExample example);
//
//    int updateByPrimaryKeySelective(Train record);
//
//    int updateByPrimaryKey(Train record);
}
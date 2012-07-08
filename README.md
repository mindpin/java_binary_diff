java_binary_diff
================

基于java实现的，以rsync算法原理为基础的二进制文件差异比较处理

2012-7-8:
  实现了基础的算法逻辑，主要包括以下三个逻辑：
    根据 src_file 计算 chunk_map;
    根据 chunk_map 和 target_file 生成 patch;
    根据 src_file 和 patch 生成与 target_file 一样的 result_file;
    
  TODO: 
    实现将 chunk_map 和 patch 数据经过enchode保存到文件，以及从文件读取。
    初步计划使用 BASE64 编码
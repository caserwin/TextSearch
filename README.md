# TextSearch
## 说明
项目最初是基于 Myeclipse 的matisse插件构建GUI。后期转为IDEA项目，可以用JFormDesigner 重新构建用户界面。

基于TF-IDF/TF两种方式对文本向量化，构建词语-文档的倒排索引。然后存到数据库中。
在查询时，把query也解析掉，过滤停止词，查询数据库中匹配的词语，把匹配到的词语权重相加，就可以得到query和文档的相关程度系数。
对这相关系数排序，得到最相关的文档。

## 搜狗语料库资料
链接: https://pan.baidu.com/s/1icoAHlMUZOZ2O3HxhPphdQ 提取码: 7tmn

## 使用演示
![](https://github.com/caserwin/TextSearch/raw/master/data/text_search_demo.gif)
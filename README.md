# oss-sync
阿里云的OSS与ECS的图片同步工具。可以自动将您所需要的同步的服务器无缝迁移到阿里云。

使用：
在JdbcUtil中修改您的本地数据库，这个数据库是用来记录哪些图片是同步过的，同步过的不需要再同步。
```sql

create table file_log(
id int auto_increment,
file_name varchar(255)

)
```
然后：
在AliyunOSSUtils中配置您的oss信息，比如key，accessid等信息
最后，在SyncFile中填写主机信息
运行。
原理：使用的java通过ssh下载ecs文件，通过你的写代码所在的机器中转，上传到oss

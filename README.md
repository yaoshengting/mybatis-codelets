# mybatis-codelets
# 说明
  1. 该工具包主要是使用mybatis来操作mysql数据库的，其他数据库还没有做兼容
  2. 工具包主要是封装了底层对于单表的增加、删除、更新，使用了乐观锁，并通过@History注解来针对每一个版本号的历史数据做快照存留到历史表中
# 依赖包
 
  1. spring相关jar包  *这个不用多说了*
  2. commons-beanutils.jar *操作对象属性时用到*
  3. guava.jar *底层jar包用到了BiMap*
  4. dozer.jar *主要是对象转换时需要引用该包的java.util.Date的日期转换，BeanUtils.setProperty默认是不支持java.util.Date格式转换的*
  5. lombok.jar *对bean生成getter和setter* 
  6. jackson.jar *序列化与反序列化*
    
# 设计规范
  1. 要根据项目的情况进行领域模型分层，必须单独分出DO、QueryVO；其他VO，DTO，BO等视情况而定
  2. 模型之间的转换可以使用的工具有
   + Dozer
   + org.springframework.beans.BeanUtils
   + org.apache.commons.beanutils.BeanUtils
   + apache的BeanUtils性能比较差，不建议使用；使用前也先了解下不同工具包属性复制的特点
# 封装的SingleTableDao对外提供的API
   + long insert(final T entry); *新增数据，返回主键；表若无主键，则返回0*
   + List<String> insertList(final List<T> entryList); *批量新增数据，返回主键List*
   + int insertListWithoutPK(final List<T> entryList); *无主键批量插入，返回条数*
   + boolean deleteByEntry(final T entry); *删除一条记录*
   + boolean deleteEntryById(final Long entryId); *根据主键删除实体数据*
   + boolean updateEntryColumnsByID(final T tobeUpdate, final Set<String> columnNameSet); *根据主键更新该entry指定字段*
   + T queryByID(final long entryID); *根据主键查询该数据记录*
   + List<T> queryAll(); *查询所有记录*
   + long queryAllCount(); *查询表中记录条数*
   + long queryCountByColumns(final Map<String, Object> columnValues); *根据指定列汇总查询条数*
   + List<T> queryListByColumns(final Map<String, Object> columnValues); *根据指定列进行查询*
   + List<T> queryListByColumnsWithOrder(final Map<String, Object> columnValues,final Map<String, OrderByEnum> orderColumns); *根据指定列查询并根据指定列排序*
   + List<T> queryListByColumnsWithOrderTopN(final List<IColumnCondition> whereCondition,final Map<String, OrderByEnum> orderColumns, final int topN); *根据指定条件查询并根据指定列排序，取结果的topN条记录*
   + List<T> queryListByColumnsWithPageOrder(final Map<String, Object> columnValues, final PageCond pageCond,final Map<String, OrderByEnum> orderColumns); *根据指定列做分页查询，并根据指定列进行排序*
   + List<T> queryListByColumnsWithPageOrder(final List<IColumnCondition> whereCondition, final PageCond pageCond,final Map<String, OrderByEnum> orderColumns); *根据指定条件分页查询并根据指定列进行排序*
   + List<T> queryListByColumnsWithPage(final Map<String, Object> columnValues, final PageCond pageCond); *根据指定列做分页查询*
   + T querySumByColumns(final Set<String> sumColumnSet, final List<IColumnCondition> whereCondition); *根据指定条件查询并汇总相应的列值*
   + T queryAggregateByColumns(final Set<IAggregateCondition> aggregateColumn,final List<IColumnCondition> whereCondition); *查询并聚合*
# 在SingleTableDao子类中可以调用的protected方法
   + protected int deleteByColumn(final Map<String, Object> columnValue); *根据列删除数据*
   + protected boolean deleteListByIdList(final List<Long> idList); *根据主键集合删除数据*
   + protected T queryOneEntryByColumns(final Map<String, Object> columnValues); *根据指定列查询出一条数据，如果是多条，则抛异常*
# 示例
 1. 业务Dao接口继承ISingleTableDao接口并指定与数据库字段队列的Entry实体类
    public interface IUserLoginInfoDao extends ISingleTableDao<UserInfoEntry> {
      UserInfoEntry queryByUserName(final String userName);
    }
 2. 业务Dao实现类继承ISingleTableDao接口的实现类SingleTableDao
   @Repository
   public class UserLoginInfoDaoImpl extends SingleTableDao<UserInfoEntry> implements IUserLoginInfoDao {
	 @Override
	 public UserInfoEntry queryByUserName(final String userName) {
		    final Map<String, Object> queryCondMap = new HashMap<String, Object>();
		    queryCondMap.put("USER_NAME", userName);
		    return queryOneEntryByColumns(queryCondMap);
	 }

# mybatis-codelets

#依赖包
 
  1.spring相关jar包  *这个不用多说了*
  2.commons-beanutils.jar *操作对象属性时用到*
  3.guava.jar *底层jar包用到了BiMap*
  4.dozer.jar *主要是对象转换时需要引用该包的java.util.Date的日期转换，BeanUtils.setProperty默认是不支持java.util.Date格式转换的*
  5.lombok.jar *对bean生成getter和setter* 
  6.jackson.jar *序列化与反序列化*
    
#设计规范
  1.要根据项目的情况进行领域模型分层，必须单独分出DO、QueryVO；其他VO，DTO，BO等视情况而定
  2.模型之间的转换可以使用的工具有
   +Dozer
   +org.springframework.beans.BeanUtils
   +org.apache.commons.beanutils.BeanUtils
   +apache的BeanUtils性能比较差，不建议使用；使用前也先了解下不同工具包属性复制的特点

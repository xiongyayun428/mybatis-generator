package com.xiongyayun.mybatis.generator;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * AladdinCommentGenerator
 *
 * @author: <a href="mailto:xiongyayun428@163.com">Yayun.Xiong</a>
 * @date: 2020/6/28
 */
public class AladdinCommentGenerator implements CommentGenerator {
    private final Properties properties;
    private Properties systemPro;
    /**
     * 父类时间
     */
    private boolean suppressDate;

    private String superInterface;

    private String author;
    private String dateFormat;

    public AladdinCommentGenerator() {
        properties = new Properties();
        systemPro = System.getProperties();
        suppressDate = false;
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        // 获取自定义的 properties
        this.properties.putAll(properties);
        this.superInterface = properties.getProperty("superInterface");
        this.author = properties.getProperty("author", systemPro.getProperty("user.name"));
        this.dateFormat = properties.getProperty("dateFormat", "yyyy-MM-dd");
    }

    private boolean isEmpty(String obj) {
        if (obj == null || "".equals(obj)) {
            return true;
        }
        return false;
    }

    private String getCurrentDate() {
        return (new SimpleDateFormat(dateFormat)).format(new Date());
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + (isEmpty(remarks) ? introspectedColumn.getActualColumnName() : remarks));
        field.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("/** ");
        String fieldName = field.getName();
        if ("orderByClause".equals(fieldName)) {
            sb.append(" 排序字段");
        } else if ("distinct".equals(fieldName)) {
            sb.append(" 过滤重复数据");
        } else if ("oredCriteria".equals(fieldName)) {
            sb.append(" 当前的查询条件实例");
        } else if ("isDistinct".equals(fieldName)) {
            sb.append(" 是否过滤重复数据");
        }
        sb.append(" */");
        field.addJavaDocLine(sb.toString());
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (superInterface != null && !"".equals(superInterface)) {
            topLevelClass.addImportedType(superInterface);
            topLevelClass.addSuperInterface(new FullyQualifiedJavaType(superInterface.substring(superInterface.lastIndexOf(".")+1,superInterface.length())));
        }

        // 获取表注释
        String remarks = introspectedTable.getRemarks();

        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + remarks);
        topLevelClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author " + author);
        topLevelClass.addJavaDocLine(" * @date   " + getCurrentDate());
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append(" ");
        sb.append(getDateString());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean b) {
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * @author ");
        sb.append(systemPro.getProperty("user.name"));
        sb.append(" ");
        sb.append(getCurrentDate());
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        innerEnum.addJavaDocLine("/**");
        innerEnum.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(" */");
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        String remarks = introspectedColumn.getRemarks();
        if (isEmpty(remarks)) {
            sb.append(introspectedColumn.getActualColumnName());
        } else {
            sb.append("获取" + remarks);
        }
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * @return ");
        sb.append(introspectedColumn.getActualColumnName());
        if (!isEmpty(remarks)) {
            sb.append(" - ");
            sb.append(remarks);
        }
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        String remarks = introspectedColumn.getRemarks();
        if (isEmpty(remarks)) {
            sb.append(introspectedColumn.getActualColumnName());
        } else {
            sb.append("设置" + remarks);
        }
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        Parameter parameter = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param ");
        sb.append(parameter.getName());
        if (!isEmpty(remarks)) {
            sb.append(" ");
            sb.append(remarks);
        }
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        method.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        sb.append(" * ");
        if (method.isConstructor()) {
            sb.append("构造查询条件");
        }

        final List<Parameter> parameterList = method.getParameters();
        String methodName = method.getName();
        if ("setOrderByClause".equals(methodName)) {
            sb.append("设置排序字段");
        } else if ("setDistinct".equals(methodName)) {
            sb.append("设置过滤重复数据");
        } else if ("getOredCriteria".equals(methodName)) {
            sb.append("获取当前的查询条件实例");
        } else if ("isDistinct".equals(methodName)) {
            sb.append("是否过滤重复数据");
        } else if ("getOrderByClause".equals(methodName)) {
            sb.append("获取排序字段");
        } else if ("createCriteria".equals(methodName)) {
            sb.append("创建一个查询条件");
        } else if ("createCriteriaInternal".equals(methodName)) {
            sb.append("内部构建查询条件对象");
        } else if ("clear".equals(methodName)) {
            sb.append("清除查询条件");
        } else if ("countByExample".equals(methodName)) {
            sb.append("根据指定的条件获取数据库记录数");
        } else if ("deleteByExample".equals(methodName)) {
            sb.append("根据指定的条件删除数据库符合条件的记录");
        } else if ("deleteByPrimaryKey".equals(methodName)) {
            sb.append("根据主键删除数据库的记录");
        } else if ("insert".equals(methodName)) {
            sb.append("新写入数据库记录");
        } else if ("insertSelective".equals(methodName)) {
            sb.append("动态字段,写入数据库记录");
        } else if ("selectByExample".equals(methodName)) {
            sb.append("根据指定的条件查询符合条件的数据库记录");
        } else if ("selectByPrimaryKey".equals(methodName)) {
            sb.append("根据指定主键获取一条数据库记录");
        } else if ("updateByExampleSelective".equals(methodName)) {
            sb.append("动态根据指定的条件来更新符合条件的数据库记录");
        } else if ("updateByExample".equals(methodName)) {
            sb.append("根据指定的条件来更新符合条件的数据库记录");
        } else if ("updateByPrimaryKeySelective".equals(methodName)) {
            sb.append("动态字段,根据主键来更新符合条件的数据库记录");
        } else if ("updateByPrimaryKey".equals(methodName)) {
            sb.append("根据主键来更新符合条件的数据库记录");
        } else if ("or".equals(methodName)) {
            if (parameterList.isEmpty()) {
                sb.append("创建一个新的或者查询条件");
            } else {
                sb.append("增加或者的查询条件,用于构建或者查询");
            }
        }
//        sb.append(",");
//        sb.append(introspectedTable.getFullyQualifiedTable());
        method.addJavaDocLine(sb.toString());

        String parameterName;
        for (Parameter parameter : parameterList) {
            sb.setLength(0);
            sb.append(" * @param ");
            parameterName = parameter.getName();
            sb.append(parameterName);
            if ("orderByClause".equals(parameterName)) {
                sb.append(" 排序字段");
            } else if ("distinct".equals(parameterName)) {
                sb.append(" 是否过滤重复数据");
            } else if ("criteria".equals(parameterName)) {
                sb.append(" 过滤条件实例");
            }
            method.addJavaDocLine(sb.toString());
        }
        method.addJavaDocLine(" * @return " + method.getReturnType().get());
        method.addJavaDocLine(" */");
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    @Override
    public void addComment(XmlElement xmlElement) {

    }

    @Override
    public void addRootComment(XmlElement xmlElement) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    protected String getDateString() {
        String result = null;
        if (!suppressDate) {
            result = getCurrentDate();
        }
        return result;
    }
}

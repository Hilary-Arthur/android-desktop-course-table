# 新增功能

## 增加消息弹窗
+ 上课前半个小时，通过系统顶栏进行消息提醒，持续时间10秒
+ 用户自定义添加考试信息，考试提前一周和提前1小时进行提醒

## 节假日调休设置
+ 用户选择节假日课程更改日期
+ 课程调整设置，用户可自主修改课程日期

# 初始化状态
+ 首次安装程序时，进入为空白页面，只有导航栏，未渲染课表，需要用户自己导入json格式
+ 导入的json的格式根据skill
+ 首次进入需要用户自主设置学期开始时间

# 重构目标
+ 将左侧课程表栏修改为时间，时间信息在个人设置当中修改，将修改的时间保存，后续如果不主动触发就保持不变

# 多版本构建

## 概述
应用将打包为两个独立版本，分别对应不同学校的课程时间配置：
- **西南大学版 (SWU)**：一天14节课
- **重庆医科大学版 (CQMU)**：一天18节课

## 实现方式
使用 Gradle `productFlavors` 实现编译时版本切换：

```groovy
// app/build.gradle
android {
    flavorDimensions "school"
    productFlavors {
        swu {
            dimension "school"
            resValue "string", "school_name", "西南大学"
            // 使用 swu_course.json 和 course_time.json 中的 swu 配置
        }
        cqmu {
            dimension "school"
            resValue "string", "school_name", "重庆医科大学"
            // 使用 cqmu_course.json 和 course_time.json 中的 cqmu 配置
        }
    }
}
```

## 配置文件
- `res/raw/swu_course.json` - 西南大学课程数据
- `res/raw/cqmu_course.json` - 重庆医科大学课程数据
- `res/raw/course_time.json` - 课程时间配置（包含两个学校的时间表）

## 构建命令
```bash
# 构建西南大学版
./gradlew assembleSwuDebug

# 构建重庆医科大学版
./gradlew assembleCqmuDebug
```

## 待完成
- [ ] 配置 productFlavors
- [ ] 修改 CourseRepository 根据 flavor 加载对应配置
- [ ] 移除设置页面中的学校选择 UI
# 课程表 App

一款简洁美观的 Android 课程表应用，支持多学校版本构建。

## 功能特性

### 核心功能
- **今日课表**：显示当天课程信息，支持左右滑动切换日期
- **周数选择**：根据学期起始日期自动计算当前周数
- **课程导入**：支持通过 JSON 格式导入自定义课表
- **考试管理**：添加、查看、删除考试信息

### 提醒功能
- **课程提醒**：上课前30分钟弹窗提醒
- **考试提醒**：考试前7天、1天、当天弹窗提醒

### 设置功能
- **学期起始日期**：设置学期开始时间，自动计算周数
- **AI 课表转换**：使用 AI（ChatGPT、Claude 等）将课表截图转换为 JSON 格式
- **学校信息**：显示当前版本对应的学校

## 多版本构建

### 概述
应用打包为两个独立版本，分别对应不同学校的课程时间配置：

| 版本 | 学校 | 每日节数 | App 名称 |
|------|------|----------|----------|
| SWU | 西南大学 | 14 节 | 课程表（西大） |
| CQMU | 重庆医科大学 | 18 节 | 课程表（重医） |

### 配置文件
- `res/raw/swu_course.json` - 西南大学课程数据
- `res/raw/cqmu_course.json` - 重庆医科大学课程数据
- `res/raw/course_time.json` - 课程时间配置（包含两个学校的时间表）

### 构建命令
```bash
# 构建西南大学版
./gradlew assembleSwuDebug

# 构建重庆医科大学版
./gradlew assembleCqmuDebug
```

### APK 输出位置
```
app/build/outputs/apk/swu/debug/app-swu-debug.apk
app/build/outputs/apk/cqmu/debug/app-cqmu-debug.apk
```

## JSON 课表格式

### 字段说明
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 课程名称 |
| day | integer | 是 | 星期几（1-7，周一到周日） |
| start | integer | 是 | 开始节次 |
| end | integer | 是 | 结束节次 |
| room | string | 否 | 教室 |
| teacher | string | 否 | 教师 |
| weeks | string | 是 | 上课周次范围 |
| type | string | 否 | 课程类型（讲课/实验/实践） |
| color | string | 否 | 显示颜色（十六进制） |

### weeks 格式示例
- `"1-16"` - 第1周到第16周
- `"2-13"` - 第2周到第13周
- `"1-9,11-16"` - 第1-9周和第11-16周
- `"1-15(单)"` - 第1到第15周的单数周
- `"2-14(双)"` - 第2到第14周的双数周
- `"6,11"` - 仅第6周和第11周

### 示例
```json
[
  {"name":"高等数学","day":1,"start":1,"end":2,"room":"A3-1103","teacher":"张三","weeks":"1-16","type":"讲课","color":"#4F46E5"},
  {"name":"高等数学","day":3,"start":3,"end":4,"room":"A3-1103","teacher":"张三","weeks":"1-16","type":"讲课","color":"#4F46E5"},
  {"name":"程序设计基础实验","day":5,"start":3,"end":4,"room":"D4-机房02","teacher":"王五","weeks":"2-15","type":"实验","color":"#10B981"}
]
```

## 技术栈

- **语言**：Kotlin
- **UI 框架**：Jetpack Compose
- **构建工具**：Gradle (Kotlin DSL)
- **最低 SDK**：Android 8.0 (API 26)
- **目标 SDK**：Android 14 (API 34)

## 项目结构

```
app/src/main/
├── java/com/example/coursetable/
│   ├── data/
│   │   ├── Course.kt          # 课程数据类
│   │   ├── CourseRepository.kt # 数据仓库
│   │   ├── CourseTime.kt       # 课程时间数据类
│   │   ├── Exam.kt            # 考试数据类
│   │   └── University.kt      # 学校枚举
│   ├── ui/
│   │   ├── CourseTableScreen.kt  # 主界面
│   │   ├── TodayCourseScreen.kt  # 今日课表
│   │   ├── SettingsScreen.kt     # 设置页面
│   │   ├── ExamScreen.kt        # 考试管理
│   │   ├── SkillGuideScreen.kt  # AI 转换教程
│   │   └── WeekSelector.kt      # 周数选择器
│   └── util/
│       └── WeekParser.kt        # 周次解析工具
└── res/raw/
    ├── swu_course.json         # 西南大学课程数据
    ├── cqmu_course.json        # 重庆医科大学课程数据
    └── course_time.json        # 课程时间配置
```

## 版本历史

### v2.0
- 多学校版本构建支持
- 今日课表左右滑动切换日期
- 课程提醒和考试提醒功能
- AI 课表转换教程
- UI 美化：圆角卡片、大字体

### v1.2
- 初始版本发布
- 基础课程表显示功能
- JSON 课表导入功能

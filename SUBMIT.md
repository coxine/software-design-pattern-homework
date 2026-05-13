# 提交要求与 Gradescope 使用指南[#](http://172.29.7.220/assignments/submission-guide.html#提交要求与-gradescope-使用指南)

本页面用于说明：

- 如何加入课程对应的 Gradescope 作业平台
- 如何准备并打包你的 `submission`
- 如何在提交后自查常见问题

------

## 1. 加入 Gradescope 课程[#](http://172.29.7.220/assignments/submission-guide.html#_1-加入-gradescope-课程)

在 Gradescope 中输入课程代码 X84BDY 加入课程

Gradescope 官网：https://www.gradescope.com/

加入后，在课程内找到本次作业（例如 `2026-Spri-Software-Arc-Assignment01`）。

> 若你无法看到作业，请先确认：
>
> - 你加入的是正确课程
> - 作业已到发布时间

------

## 2. 本次作业提交内容[#](http://172.29.7.220/assignments/submission-guide.html#_2-本次作业提交内容)

你需要提交 **Java 源码**，入口类必须为 `Main`（文件名 `Main.java`）。

允许多文件实现，建议结构如下：

text

```
submission/
  src/
    Main.java
    MemFs.java
    CliParsers.java
```

也可以把 `Main.java` 放在提交包根目录，但必须保证：

- `Main.java` 存在
- 所有依赖的 `.java` 文件一并提交
- 不要提交编译产物（如 `.class`）

------

## 3. 打包 submission（推荐 zip）[#](http://172.29.7.220/assignments/submission-guide.html#_3-打包-submission-推荐-zip)

假设你当前目录为项目根目录，且源码在 `src/`：

bash

```
mkdir -p submission/src
cp src/*.java submission/src/
zip -r submission.zip submission
```

如果你是其它目录结构，请确保 zip 解压后仍能找到 `Main.java` 和相关 `.java` 文件。

------

## 4. 在 Gradescope 提交[#](http://172.29.7.220/assignments/submission-guide.html#_4-在-gradescope-提交)

1. 打开对应作业页面
2. 点击 `Upload Submission`
3. 上传你的 `submission.zip`
4. 等待自动评测完成并查看结果

你可以多次提交，系统以最新一次提交为准。

评测结果说明：

- hidden case 的**具体输入输出与逐条失败细节仍会隐藏**；
- 但结果页会额外显示一个可见汇总项 `hidden-summary`，用于告知你 hidden case 的通过情况（例如 `Hidden cases passed: 6/8`）。

------

## 5. 提交后自查清单[#](http://172.29.7.220/assignments/submission-guide.html#_5-提交后自查清单)

- 能否本地编译：`javac *.java` 或 `javac src/*.java`
- 程序是否从 `stdin` 读取、向 `stdout` 输出
- 是否严格按题面命令格式处理输入
- 是否存在本地路径依赖或硬编码输出

------

如遇平台异常（上传后构建失败等），请尽快截图并联系助教652025320006@smail.nju.edu.cn。
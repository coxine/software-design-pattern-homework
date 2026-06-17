实验：微型内存文件系统（迭代二）#
时间安排#
第二次迭代截止时间（暂定）：6月21日 0 a.m.
实验背景#
本次迭代是“微型内存文件系统”实验的最后一次迭代。你需要在迭代一的基础上扩展文件系统能力，使其支持更接近真实文件系统的路径处理、递归搜索、删除操作和链接机制。

程序仍然通过标准输入读取命令，通过标准输出返回查询结果。建议继续使用 Java 实现。

迭代二：路径规范化、搜索、删除与链接#

1. 领域模型设定#
系统只有一个根目录，路径用 / 表示。

文件（File）：拥有名称、内容大小（整数，模拟字节数）。
目录（Directory）：拥有名称，可以包含多个文件、目录或链接。
链接（Link）：拥有名称，指向一个已经存在的文件、目录或链接。
目录大小为其可达子树内文件大小之和。由于链接会带来共享节点，同一个底层文件或目录通过多条路径可达时，INFO 统计中只计算一次。

1. 路径规则#
所有命令中的路径都必须是绝对路径，即以 / 开头。

迭代二要求对所有路径先做规范化，再执行命令：

允许冗余 /，例如 //usr///local 等价于 /usr/local。
支持 .，表示当前目录。
支持 ..，表示父目录；根目录的父目录仍为根目录。
非根路径末尾的 / 允许出现，例如 /usr/local/ 等价于 /usr/local。
示例：

text
/a//b/./c/../
规范化后等价于：

text
/a/b
如果路径不是绝对路径，则该命令无效，直接忽略，不输出任何内容。

1. 指令集#
MKDIR <绝对路径>#
创建目录。

如果父目录不存在，忽略该命令。
如果父路径解析后不是目录，忽略该命令。
如果目标路径已存在文件或链接，用新目录替换该目录项。
如果目标路径已存在目录，保持不变。
不允许创建或替换根目录。
TOUCH <绝对路径> <大小>#
创建文件并指定大小。

<大小> 必须是非负整数；否则忽略该命令。
如果父目录不存在，忽略该命令。
如果父路径解析后不是目录，忽略该命令。
如果目标路径已存在文件，覆盖其大小。
如果目标路径已存在目录或链接，用新文件替换该目录项。
不允许创建或替换根目录。
LS <绝对路径>#
列出路径对应节点。

如果目标是文件，输出该文件名。
如果目标是目录，输出其所有直接子节点名称，按字典序升序排列，每行一个名称。
如果目标是链接，先跟随链接，再按被链接目标的类型处理：
链接指向文件时，输出链接自身的名称；
链接指向目录时，输出目标目录的直接子节点名称。
如果路径不存在，忽略该命令。
INFO <绝对路径>#
输出节点大小。

文件大小为创建或覆盖时给定的大小。
目录大小为其可达子树内文件大小之和。
链接大小为其目标节点大小。
统计目录大小时，同一个底层文件或目录只计算一次，避免链接带来的重复计数。
如果路径不存在，忽略该命令。
FIND <绝对路径> <name>#
在路径对应节点中递归查找名称等于 <name> 的节点，并输出匹配节点的绝对路径。

如果起点是文件或链接到文件，只检查该起点自身名称是否匹配。
如果起点是目录或链接到目录，递归搜索该目录子树。
链接节点本身也可以作为匹配结果，匹配的是链接名称。
输出路径按字典序升序排列，每行一个。
没有匹配结果时，不输出任何内容。
如果路径不存在，忽略该命令。
说明：当搜索遇到链接到目录的节点时，需要进入该链接指向的目录继续搜索；为避免共享目录被重复展开，同一个底层目录在一次 FIND 中最多展开一次。

RM <绝对路径>#
删除路径对应的目录项。

删除文件：直接删除。
删除链接：只删除链接本身，不影响被链接目标。
删除目录：只有目录为空时才允许删除；非空目录忽略该命令。
如果路径不存在，忽略该命令。
不允许删除根目录。
LINK <srcAbsPath> <dstAbsPath>#
在 <dstAbsPath> 创建一个链接，指向 <srcAbsPath> 所在节点。

<srcAbsPath> 必须存在。
<dstAbsPath> 的父目录必须存在。
如果目标路径已存在文件、目录或链接，用新链接替换该目录项。
不允许创建或替换根目录。
链接采用“共享底层节点”的语义：链接不是复制文件或目录，而是让目标目录项指向已有节点。因此，对同一个底层节点建立多个链接后，INFO 应避免重复统计。

1. 错误处理统一约定#
本实验不要求输出错误信息。所有非法命令、非法参数、失败操作均直接忽略，不输出任何内容，也不改变文件系统状态。

常见忽略场景包括：

命令名称未知或参数个数不匹配；
路径不是绝对路径；
需要的父目录不存在；
对不存在路径执行 LS / INFO / FIND / RM；
TOUCH 的大小不是合法非负整数；
删除非空目录；
对根目录执行 MKDIR / TOUCH / RM / LINK 目标创建。
5. 可输出指令的“单指令”示例#
说明：下面每个示例的 Standard Input 都包含少量“前置建树指令”用于构造场景；你只需要关注最后一条或最后几条会产生输出的指令及其输出规则。

路径规范化示例：冗余 /、.、.. 与尾斜杠#
Standard Input:

text
MKDIR /usr
MKDIR //usr///local
TOUCH /usr/local/./a.txt 10
TOUCH /usr/local/../b.txt 5
LS /usr//local/
INFO /usr/./local/../
Standard Output:

text
a.txt
15
说明：

//usr///local 会规范化为 /usr/local；
/usr/local/./a.txt 会规范化为 /usr/local/a.txt；
/usr/local/../b.txt 会规范化为 /usr/b.txt；
/usr/./local/../ 会规范化为 /usr，因此目录大小为 10 + 5 = 15。
FIND 示例 1：在目录子树中递归搜索#
Standard Input:

text
MKDIR /src
MKDIR /src/main
MKDIR /src/test
TOUCH /src/main/App.java 10
TOUCH /src/test/App.java 20
TOUCH /src/test/Helper.java 5
FIND /src App.java
Standard Output:

text
/src/main/App.java
/src/test/App.java
FIND 示例 2：从文件起点搜索#
Standard Input:

text
TOUCH /readme.md 50
FIND /readme.md readme.md
FIND /readme.md other.md
Standard Output:

text
/readme.md
说明：当起点是文件时，只检查该文件本身；第二条 FIND 无匹配，因此不输出。

RM 示例 1：删除文件与空目录#
Standard Input:

text
MKDIR /tmp
TOUCH /tmp/a.bin 7
LS /tmp
RM /tmp/a.bin
LS /tmp
RM /tmp
LS /
Standard Output:

text
a.bin
说明：删除文件后 /tmp 变为空目录，可以继续删除；后两条 LS 查询到空目录或空根目录，因此不输出。

RM 示例 2：非空目录不能删除#
Standard Input:

text
MKDIR /tmp
MKDIR /tmp/cache
TOUCH /tmp/cache/a.bin 7
RM /tmp/cache
LS /tmp
Standard Output:

text
cache
说明：/tmp/cache 非空，RM /tmp/cache 被忽略。

LINK 示例 1：链接到文件#
Standard Input:

text
TOUCH /data.bin 12
LINK /data.bin /copy
LS /
LS /copy
INFO /copy
Standard Output:

text
copy
data.bin
copy
12
说明：LS /copy 输出链接自身名称 copy，INFO /copy 返回被链接文件大小。

LINK 示例 2：链接到目录并通过链接访问目录内容#
Standard Input:

text
MKDIR /real
TOUCH /real/a.txt 10
LINK /real /view
LS /view
TOUCH /view/b.txt 5
LS /real
INFO /view
Standard Output:

text
a.txt
a.txt
b.txt
15
说明：/view 指向 /real，因此通过 /view/b.txt 创建的文件实际出现在 /real 对应的底层目录中。

INFO 示例：链接共享节点不重复计数#
Standard Input:

text
MKDIR /data
TOUCH /data/a 10
TOUCH /data/b 20
LINK /data /alias
INFO /data
INFO /alias
INFO /
Standard Output:

text
30
30
30
说明：根目录下同时有 /data 和 /alias 两个目录项，但它们可达同一个底层目录；INFO / 中 /data/a 和 /data/b 只计算一次。

覆盖语义示例：文件、目录、链接可以互相替换#
Standard Input:

text
TOUCH /x 5
LINK /x /y
INFO /
TOUCH /y 30
INFO /
MKDIR /y
LS /
INFO /
Standard Output:

text
5
35
x
y
5
说明：

LINK /x /y 后，/x 和 /y 指向同一个底层文件，所以 INFO / 为 5；
TOUCH /y 30 用新文件替换 /y 这个目录项，不会修改 /x；
MKDIR /y 再用空目录替换 /y，因此最后根目录大小只剩 /x 的 5。
6. 标准输入输出示例#
Standard Input:

text
MKDIR /usr
MKDIR //usr///local
TOUCH /usr/local/./a.txt 10
TOUCH /usr/local/../b.txt 5
LINK /usr/local /alias
LS /
INFO /
FIND / a.txt
RM /alias
LS /
Standard Output:

text
alias
usr
15
/usr/local/a.txt
usr
说明：

//usr///local 会规范化为 /usr/local。
/usr/local/../b.txt 会规范化为 /usr/b.txt。
/alias 链接到 /usr/local，因此 INFO / 不会重复统计同一底层目录。
RM /alias 只删除链接，不删除 /usr/local。

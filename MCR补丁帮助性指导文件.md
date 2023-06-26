# MCR补丁帮助性指导文件

#### 前言

MCR补丁是由cdc开发的一个辅助性工具,主要用于辅助mcr大陆用户的开发

配置文件放置在configs(此文件夹会在第一次启动后生成)目录下,文件名称为enable.properties

配置文件目前能够即时的载入变更的时效性配置(模块启用和特别注明的都是非时效性配置),所以在启动mcreator时您也同样能改配置

#### 安装方法

1. 把压缩包内所有文件都拖进mcreator的安装目录

2. 使用mcreatorWithPatch.bat启动

3. 如果您需要快捷方式启动(可以像图片这样配置)

   ![image-20230215122347343](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230215122347343.png)

#### 模块目录

1. 官网屏蔽模块
2. 控制台字体大小调整模块
3. 自动翻译器模块
4. 字体修复模块
5. 汉化池模块

#### 模块详细介绍

##### 官网屏蔽模块

```properties
#模块启用
OfficialBan = true
```

官网屏蔽模块的作用跟它的名字一样,用于屏蔽官网的相关内容(因为部分大陆用户无法进入官网,这就导致了mcr无法正常启动)

##### 控制台字体大小调整模块

```properties
#模块启用
ConsoleFontSize = true 
#字体大小,你可能需要重启工作区来看效果...
ConsoleFontSize.size = 9
```

控制台字体默认情况下是很小的,经过很多用户向我反馈,小到瞎眼

这个模块就是用来调整控制台字体大小

##### 自动翻译器模块

```properties
#模块启用
Translator = true
#是否自动翻译您的mod语言文件 非时效性配置
Translator.autoTranslate = false
#翻译器引擎,目前只支持Han
Translator.engine = Han
#是否允许翻译器自动将英文翻译为中文
Translator.toCN = true
#是否允许翻译器自动将中文翻译为英文
Translator.toEN = true
#是否允许翻译器将简体中文翻译为繁体中文
Translator.toHant = true
#翻译基准,支持 current other
Translator.base = current
```

**该模块会添加一个按钮帮助您一键翻译**

![image-20230212143750973](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230212143750973.png)

**翻译基准**

另外我再讲解一下翻译基准,这个可能有点难得理解

这个意思其实是翻译文本的原始来源

如果被设置为current则代表文本来源于当前所要翻译的目标语言,而other则代表文本来源于翻译的原始语言

举个例子,下面是一个测试的表格,我们的翻译模式是英文转中文

| en_us       | zh_cn |
| ----------- | ----- |
| Grass Block | Grass |

如果当前的翻译基准是current,那么表格将会被翻译为

| en_us       | *zh_cn* |
| ----------- | ------- |
| Grass Block | 草      |

如果当前的翻译基准是other,那么表格将会被翻译为

| *en_us*     | zh_cn  |
| ----------- | ------ |
| Grass Block | 草方块 |

(斜体标明了翻译原始文本来源)

另外本模块会自动判断原始文本是否为可翻译语言(比如 中译英 原始文本只能是中文,如果不是则会保持原样)

**翻译辅助窗口**

模块会在一些特定的文本输入域添加一个快捷键:按2次右键

通过按两下快捷键会调出以下的窗口:

注:如果这个文本域内已经存在文本,那么翻译结果将会追加到后面

![image-20230215122711603](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230215122711603.png)

特定的文本输入域:

![image-20230215122806329](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230215122806329.png)

##### 字体修复模块

```properties
#模块启用
FontFix = true
#修复中文字体乱码
FontFix.chineseFont = true
#字体大小调整
FontFix.fontSizeEdit = false
#强制字体大小(这个值是瞎填的)
FontFix.fontSize = 19
```

用于修复字体不支持中文所导致的问题,注意:本模块的原理是修改字体,所以可能会导致字体变得不一样啦..

开启前:

![image-20230212144822356](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230212144822356.png)

开启后:

![image-20230212144912290](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230212144912290.png)

##### 汉化池模块(实验性)

```properties
#模块启用
CNPool = true
#强制使用内部自带的汉化池文件(加速启动,但无法自动更新)
CNPool.forceInnerPool=false
```

汉化池模块是实验功能,我们不能保证不会出现问题

它的功能就是翻译mcr一些没有翻译的中文

未启用:

![image-20230212150221815](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230212150221815.png)

启用后:

![image-20230212150303783](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230212150303783.png)

(并不只这一个)

#### 问题及对应的模块

| 问题                                                         | 对应模块       |
| ------------------------------------------------------------ | -------------- |
| MCreator卡在了进度条,类似于![img](https://tiebapic.baidu.com/forum/pic/item/92ac155c1038534355c344e6d613b07ecb808875.jpg?tbpicau=2023-04-09-05_cd94fcad53d86d4740fc7f299c4a182f) | 官网屏蔽       |
| 控制台字体太小                                               | 控制台字体调整 |
| 想要能够用中文搜索方块或者物品?                              | 汉化池         |


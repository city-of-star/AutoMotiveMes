本文旨在教会大家如何创建自己的github仓库和上传文件，并记录一些常用的 git 操作命令

  

git 下载网址：(https://git-scm.com/downloads)
![[Pasted image 20250304135605.png]]
安装的时候一直点击next即可，全部选择默认选项

  

# 第一步 创建 github 仓库

## 1. 打开 github 官网，点击 New repository

![[Pasted image 20250304140249.png]]

  

## 2. 输入仓库名称，其他选项均默认，然后点击 Create repository

![[Pasted image 20250304135653.png]]

  

## 3. 创建好的新仓库长这样

![[Pasted image 20250304135711.png]]

  

# 第二步 使用 git 上传项目

## 1. 找到你想上传到 github 的项目或者文件夹，然后双击点开他

![[Pasted image 20250304135729.png]]

  

## 2. 右击空白处选择 Open Git Bash here（如果你用的是 win11 那么你需要先选择显示更多选项才能看到）

![[Pasted image 20250304135749.png]]

  

## 3. git bash 长这个样子

![[Pasted image 20250304135806.png]]
![image](/file/52/bT2iyAz0bq15jd-3LSNil.png)

  

## 4. 创建 .git 文件夹（初始化仓库）

温馨提示：gitbash不能用ctrl+c和ctrl+v来复制粘贴，你需要右击选择copy和paste来复制粘贴

```bash

git init

```

![[Pasted image 20250304135828.png]]
![[Pasted image 20250304135840.png]]
此文件夹默认是隐藏的，需要显示隐藏的项目才能看见

  

## 5. 配置 Git 用户信息，包括用户名和邮箱，这些信息会记录在提交的每一次记录中  （--global 表示全局注册，如果只是需要在本项目就可以去掉 --global）

```bash

git config --global user.name "Your Name"

git config --global user.email "your.email@example.com"

```

![[Pasted image 20250304135913.png]]

  

## 6. 查看当前项目中文件的状态，了解哪些文件是未跟踪的（untracked）、已修改的（modified）

```bash

git status

```

![[Pasted image 20250304135924.png]]

红色表示该文件还未上传

  

## 7. 将所有未上传的文件提交到暂存区

```bash

git add .

```

![[Pasted image 20250304135937.png]]

  

## 8. 将暂存区的内容提交到本地仓库（.git 文件夹），并且给予描述

```bash

git commit -m "Add README file"

```

![[Pasted image 20250304135953.png]]

  

## 9. 将本地仓库与远程仓库关联起来，记得换成你自己仓库的链接，不要直接使用下面的命令

```bash

git remote add origin https://github.com/city-of-star/test.git

```

![[Pasted image 20250304140010.png]]
![[Pasted image 20250304140021.png]]

  

## 10. 查看本地分支的名称（默认是 master）

```bash

git branch

```

![[Pasted image 20250304140038.png]]

  

## 11. 当前分支名称是 master，可以将其重命名为 main

```bash

git branch -m master main

```

![[Pasted image 20250304140048.png]]

  

## 12. 拉取远程仓库的更新，并尝试自动合并到本地分支（就是把 github 仓库的内容拉取到本地，在提交代码之前先拉取，防止代码冲突，第一次提交的时候可以不拉取）

```bash

git pull origin main

```

![[Pasted image 20250304140059.png]]

  

## 13. 将本地仓库的内容推送到远程仓库（首次推送）

```bash

git push -u origin main

```

![[Pasted image 20250304140111.png]]

![[Pasted image 20250304140123.png]]

  

至此就完成了全过程，后面如果你还想上传新的文件，重复步骤6、7、8、12、13即可，并且步骤13可以简写为

```bash

git push

```

因为 -u origin main 指定了分支，后面就不需要重复指定分支了

  
  
  

🔧 进阶技巧

1. 分支管理

```bash

git branch feature/login       # 创建新分支

git checkout feature/login     # 切换分支

git merge main                 # 合并分支到当前分支

git branch -d old-branch       # 删除本地分支

```

  

2. 版本回退

```bash

git log --oneline              # 查看提交历史

git reset --hard HEAD~1        # 回退到上一个版本

git push -f                    # 强制推送（慎用！）

```

  

3. 敏感文件保护

敏感文件就是你不希望上传到github上的文件，比如一些个人隐私或者项目中的日志、外部库等
在根目录下创建.gitignore文件：

```bash

# 示例内容

*.log

node_modules/

.env

.DS_Store

```
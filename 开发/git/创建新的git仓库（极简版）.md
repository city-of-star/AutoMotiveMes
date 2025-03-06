1. 创建 .git 文件夹（初始化仓库）
```
git init
```

2. 配置 Git 用户信息，包括用户名和邮箱，这些信息会记录在提交的每一次记录中（--global 表示全局注册，如果只是需要在本项目就可以去掉 --global）
```
git config --global user.name "Your Name" 
git config --global user.email "your.email@example.com"
```

3. 创建 README 文件
```
touch README.md
echo "This is my new project" > README.md
```

4. 查看当前项目中文件的状态，了解哪些文件是未跟踪的（untracked）、已修改的（modified）
```
git status
```

5. 将文件添加到暂存区
```
git add .
```

6. 将暂存区的更改提交到本地仓库，并且给予描述
```
git commit -m "Add README file"
```

7. 将本地仓库与远程仓库关联起来
```
git remote add origin https://github.com/yourusername/your-repo.git
```

8. 查看本地分支
```
git branch
```

9. 当前分支名称是 master，可以将其重命名为 main
```
git branch -m master main
```

10. 拉取远程仓库的更新，并尝试自动合并到本地分支
```
git pull origin main
```

1. 将本地仓库的内容推送到远程仓库
```
git push -u origin main
```


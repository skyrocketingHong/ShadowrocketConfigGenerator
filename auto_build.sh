#!/bin/bash

Path=/root/gitProject/Shadowrocket.Config
cd $Path

git fetch --all
git reset --hard origin/master

java -jar Shadowrocket.Config.jar

git add .
git commit -m "Nightly build" -m "已合并最新的去广告规则及 GFWList"
git push

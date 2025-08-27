FROM nginx:1.28-alpine

# 复制Nginx配置文件
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf

# 复制构建好的静态文件到Nginx的默认静态目录
COPY ./dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
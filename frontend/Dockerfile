# 使用Node.js作为构建环境
FROM node:18-alpine AS build

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package.json .
COPY package-lock.json .

# 安装依赖
RUN npm install

# 复制源代码
COPY . .

# 构建Vue项目
RUN npm run build

# 使用Nginx作为运行环境
FROM nginx:alpine

# 复制Nginx配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 复制构建好的静态文件到Nginx的默认静态目录
COPY --from=build /app/dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
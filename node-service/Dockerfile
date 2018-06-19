FROM node:8

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY package.json .
COPY Gruntfile.js .

RUN npm install --production

EXPOSE 8081
CMD ["npm","start"]
FROM node:14-buster-slim

RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y build-essential curl unzip

RUN curl "https://d1vvhvl2y92vvt.cloudfront.net/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
RUN unzip awscliv2.zip
RUN ./aws/install
RUN rm awscliv2.zip

RUN npm i -g @aws-amplify/cli
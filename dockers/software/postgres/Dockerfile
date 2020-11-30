FROM postgis/postgis:12-master

RUN localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8

ENV LANG zh_CN.utf8
ENV TZ Asia/Shanghai

#COPY sources.list /etc/apt/
COPY .pgpass /root/.pgpass
COPY init.sh /docker-entrypoint-initdb.d/
COPY init.sql /data/
COPY yj_platform-ddl.sql /data/

RUN chmod 0600 /root/.pgpass
#&& apt-get -y update && \
#  apt-get -y install git default-libmysqlclient-dev postgresql-server-dev-12 make gcc clang && \
#  git clone --depth=1 https://github.com/EnterpriseDB/mysql_fdw.git && \
#  cd mysql_fdw && make USE_PGXS=1 && make USE_PGXS=1 install && \
#  cd .. && rm -R mysql_fdw && \
#  apt-get clean


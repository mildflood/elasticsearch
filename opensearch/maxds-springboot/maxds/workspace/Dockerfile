FROM dockerdev.cicd.prdoit-cuse1.aces.sec.gov/oit/sharedservices/acesal2golden:20220201
RUN yum update -y && \
  echo "Installing Java" && \
  JAVA_11_HOME="/usr/lib/jvm/java-11-amazon-corretto" && \
  JDK_11_HOME="/usr/lib/jvm/java-11-amazon-corretto" && \
  JRE_11_HOME="/usr/lib/jvm/java-11-amazon-corretto" && \
  export JAVA_HOME="$JAVA_11_HOME" && \
  export JDK_HOME="$JDK_11_HOME" && \
  export JRE_HOME="$JRE_11_HOME" && \
  export GNUPGHOME="$(mktemp -d)" && \
  curl -fL -o corretto.key https://yum.corretto.aws/corretto.key && \
  gpg --batch --import corretto.key && \
  gpg --batch --export --armor '6DC3636DAE534049C8B94623A122542AB04F24E3' > corretto.key && \
  rpm --import corretto.key && \
  rm -r "$GNUPGHOME" corretto.key && \
  curl -fL -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo && \
  grep -q '^gpgcheck=1' /etc/yum.repos.d/corretto.repo && \
  echo "priority=9" >> /etc/yum.repos.d/corretto.repo && \
  yum install -y java-11-amazon-corretto-devel-11.0.15.9-1 && \
  (find /usr/lib/jvm/java-11-amazon-corretto -name src.zip -delete || true) && \
  for tool_path in $JAVA_HOME/bin/*; do \
    tool=`basename $tool_path`; \
    update-alternatives --install /usr/bin/${tool} ${tool} ${tool_path} 10000; \
    update-alternatives --set ${tool} ${tool_path}; \
  done; \
  rm $JAVA_HOME/lib/security/cacerts; \
  ln -s /etc/pki/java/cacerts $JAVA_HOME/lib/security/cacerts; \
  /opt/busybox/wget https://nexus-repo.cicd.prdoit-cuse1.aces.sec.gov/repository/dera-maxds-maven2-dev/gov/sec/idap/maxds-opensearch/0.0.27/maxds-opensearch-0.0.27.war -O /opt/container-work-init/maxds-opensearch.war && \
  echo "/usr/lib/jvm/java-11-amazon-corretto/lib" >> /etc/ld.so.conf.d/java.conf && \
  ldconfig && \
  yum clean all && \
  rm -rf /var/cache/yum && \
  rm -rf /tmp/* && \
  rm -rf /var/tmp/*

EXPOSE 8080/tcp

CMD ["/usr/bin/env", "JAVA_HOME=/usr/lib/jvm/java-11-amazon-corretto", "/usr/bin/java", "-jar -Dspring.profiles.active=ci","/opt/container-work-init/maxds-opensearch.war"]

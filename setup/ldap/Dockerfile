FROM osixia/openldap:1.4.0

ENV LDAP_ORGANISATION="Governo do Estado de MT" \     
    LDAP_DOMAIN="mt.gov.br"

COPY bootstrap.ldif /container/service/slapd/assets/config/bootstrap/ldif/custom/50-bootstrap.ldif

EXPOSE 389

ENTRYPOINT ["/container/tool/run"]

IMPORTANT NOTES#

LEGEND : XXX = REMOVED

LIST OF PORTS OF MICROSERVICE

PORTS           :           DETAILS

1101            :           ACCOUNT-SERVICE
1102            :           WORKORDER-SERVICE
1103            :           SYSTEM-SERVICE
1104            :           tools-and-equipment-service
1105            :           fixed-assets-service
1106            :           summary-report-service
1107            :           rental-assets-service
1108            :           te-ledger-service
1109            :           scheduler-service
1110            :           ipgfa-ledger-service

8761            :           SERVICE-REGISTRY

8090            :           EDGE-SERVICE
8080            :           NOT AVAILABLE
3389            :           REACT-UI | XXX
8070            :           USER-INTERFACE | XXX
8060            :           HYSTRIX-DASHBOARD (NOT YET READY) | XXX
8050            :           SPRINGBOOT-ADMIN
8040            :           CONFIG-SERVER
8030            :           AUTH-SERVICE
8081            :           FLOW-SERVICE

9411            :           ZIPKIN SERVER

//========================================================================
RUN THE PROJECT IN THIS SEQUENTIAL STEPS ###

001 ESSENTIALS
    SERVICE-REGISTRY
    CONFIG-SERVER
        ZIPKIN SERVER (OPTIONAL)
        SPRINGBOOT-ADMIN (OPTIONAL)

002 SERVICES
    ACCOUNT-SERVICE | XXX
    WORKORDER-SERVICE | XXX
    SYSTEM-SERVICE
    tools-and-equipment-service
    fixed-assets-service
    summary-report-service
    rental-assets-service
    te-ledger-service

003 FRONT END
    EDGE-SERVICE
    AUTH-SERVICE

004 FRONTEND/SAKAI-REACT/ NPM START
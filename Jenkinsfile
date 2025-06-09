pipeline {

    agent any
    tools {
            maven 'MAVEN_HOME'
            jdk 'JAVA_JDK_11'
     }

    environment {
            SHOW_ENV_VAR = '0'

            BUILD_SERVICE_REGISTRY = '0'
            BUILD_CONFIG_SERVER = '0'
            BUILD_SPRINGBOOT_ADMIN = '0'
            TRANSFER_ZIPKIN_SERVICE = '0'

            BUILD_SYSTEM_SERVICE = '0'
            BUILD_SUMMARY_REPORT = '0'
            BUILD_FIXED_ASSET = '0'
            BUILD_RENTAL_ASSET = '0'
            BUILD_TOOLS_EQUIP = '0'

            BUILD_AUTH_SERVICE = '0'
            BUILD_EDGE_SERVICE = '0'
            BUILD_USER_FRONTEND = '1'

            TEST_BUILD = '0'

    }

    stages {

        stage ('Show Env Variables') {
            when { expression { SHOW_ENV_VAR == '1' } }
            steps{
                bat "set"
            }
        }
//==============================================
// ESSENTIALS
//==============================================
        stage('Build SERVICE_REGISTRY') {
            when { expression { BUILD_SERVICE_REGISTRY == '1' } }
            steps {
                dir("${WORKSPACE}\\service-registry"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\service-registry\\target"){
                        bat '''
                            echo "Copy service registry jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-01ESSENTIALS/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build CONFIG_SERVER') {
            when { expression { BUILD_CONFIG_SERVER == '1' } }
            steps {
                dir("${WORKSPACE}\\config-server"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\config-server\\target"){
                        bat '''
                            echo "Copy config-server jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-01ESSENTIALS/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build SPRINGBOOT_ADMIN') {
            when { expression { BUILD_SPRINGBOOT_ADMIN == '1' } }
            steps {
                dir("${WORKSPACE}\\springboot-admin"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\springboot-admin\\target"){
                        bat '''
                            echo "Copy springboot-admin jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-01ESSENTIALS/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Transfer ZIPKIN SERVICE') {
            when { expression { TRANSFER_ZIPKIN_SERVICE == '1' } }
            steps {
                dir("${WORKSPACE}\\TOOLS"){
                    bat '''
                        echo "Copy zipkin-service jar to folder deployment"
                        copy \"*.jar\" \"C://InventorySystemFiles/Application/F-01ESSENTIALS/\"
                    '''
                }
            }
        }
//==============================================
// MAIN SERVICES
//==============================================
        stage('Build SYSTEM_SERVICE') {
            when { expression { BUILD_SYSTEM_SERVICE == '1' } }
            steps {
                dir("${WORKSPACE}\\system-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\system-service\\target"){
                        bat '''
                            echo "Copy system-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-02SERVICES/\"
                        '''
                    }
                }
            }
        }

        //==============================================
        // MAIN SERVICES
        //==============================================
                stage('Build INVENTORY_SERVICE') {
                    when { expression { BUILD_SYSTEM_SERVICE == '1' } }
                    steps {
                        dir("${WORKSPACE}\\inventory-service"){
                            bat 'mvn clean install -Dmaven.test.skip=true'
                        }
                    }
                    post {
                        success {
                            dir("${WORKSPACE}\\inventory-service\\target"){
                                bat '''
                                    echo "Copy inventory-service jar to folder deployment"
                                    copy \"*.jar\" \"C://InventorySystemFiles/Application/F-02SERVICES/\"
                                '''
                            }
                        }
                    }
                }

//==============================================
        stage('Build SUMMARY_REPORT') {
            when { expression { BUILD_SUMMARY_REPORT == '1' } }
            steps {
                dir("${WORKSPACE}\\summary-report-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\summary-report-service\\target"){
                        bat '''
                            echo "Copy summary-report-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-02SERVICES/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build FIXED_ASSET') {
            when { expression { BUILD_FIXED_ASSET == '1' } }
            steps {
                dir("${WORKSPACE}\\fixed-assets-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\fixed-assets-service\\target"){
                        bat '''
                            echo "Copy fixed-assets-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-02SERVICES/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build RENTAL_ASSET') {
            when { expression { BUILD_RENTAL_ASSET == '1' } }
            steps {
                dir("${WORKSPACE}\\rental-assets-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\rental-assets-service\\target"){
                        bat '''
                            echo "Copy rental-assets-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-02SERVICES/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build TOOLS_EQUIP') {
            when { expression { BUILD_TOOLS_EQUIP == '1' } }
            steps {
                dir("${WORKSPACE}\\tools-and-equipment-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\tools-and-equipment-service\\target"){
                        bat '''
                            echo "Copy tools-and-equipment-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-02SERVICES/\"
                        '''
                    }
                }
            }
        }
//==============================================
// FRONTEND
//==============================================
        stage('Build AUTH_SERVICE') {
            when { expression { BUILD_AUTH_SERVICE == '1' } }
            steps {
                dir("${WORKSPACE}\\auth-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\auth-service\\target"){
                        bat '''
                            echo "Copy auth-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-03FRONTEND/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build EDGE_SERVICE') {
            when { expression { BUILD_EDGE_SERVICE == '1' } }
            steps {
                dir("${WORKSPACE}\\edge-service"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\edge-service\\target"){
                        bat '''
                            echo "Copy edge-service jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-03FRONTEND/\"
                        '''
                    }
                }
            }
        }
//==============================================
        stage('Build USER_FRONTEND') {
            when { expression { BUILD_USER_FRONTEND == '1' } }
            steps {
                dir("${WORKSPACE}\\user-frontend"){
                    bat 'mvn clean install -Dmaven.test.skip=true'
                }
            }
            post {
                success {
                    dir("${WORKSPACE}\\user-frontend\\target"){
                        bat '''
                            echo "Copy user-frontend jar to folder deployment"
                            copy \"*.jar\" \"C://InventorySystemFiles/Application/F-03FRONTEND/\"
                        '''
                    }
                }
            }
        }
//==============================================

        /*stage('TEST RUN') {
            steps {
                dir("C://InventorySystemFiles/Application/F-01ESSENTIALS/"){
                    bat '''
                        echo "test run"
                        start.bat

                    '''
                }
            }
        }*/
//==============================================

    }

}
pipeline{
    agent any
    tools{
        maven 'maven-3.8.6'
    }
    environment{
        registryCredential = 'aws_credential'
        projectName = 'url-shortener'
    }
    stages{
        stage('Maven build for services'){
            steps{
                script{
                    for(service_name in service_names.tokenize(',')){
                        if(service_name=="parent"){
                            sh 'mvn clean install -Dmaven.test.skip=true'
                        }else if(service_name=="url-common"){
                            sh 'mvn -f url-common clean install -Dmaven.test.skip=true'
                        }else{
                            sh "mvn -f ${service_name} clean package -Dmaven.test.skip=true"
                        }
                    }
                }

            }
        }
        stage("Image build and upload"){
            steps{
                script{
                    for(service_name in service_names.tokenize(',')){
                        if(service_name!="parent" && service_name!="url_common"){
                            sh "~/docker_rm.sh ${service_name}"
                            def version = sh script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
                            sh "cd ${service_name} && docker build -t ${imageRegistry}/${projectName}-${service_name}:${version} ."
                            docker.withRegistry("https://${imageRegistry}", "${imageRegistryZone}:${registryCredential}") {
                                docker.image("${imageRegistry}/${projectName}-${service_name}:${version}").push()
                            }
                            if(service_name=="eureka-server01"){
                                sh "docker run -d --name ${service_name} -p 8761:8761 ${imageRegistry}/${projectName}-${service_name}:${version}"
                                sh "docker network connect eureka-network ${service_name}"
                            }else if(service_name=="eureka-server02"){
                                sh "docker run -d --name ${service_name} -p 8762:8762 ${imageRegistry}/${projectName}-${service_name}:${version}"
                                sh "docker network connect eureka-network ${service_name}"
                            }
                        }
                    }
                }
            }
        }

    }
    post {
        // Clean after build
        always {
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
}
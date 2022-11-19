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
                    sh 'mvn clean install -Dmaven.test.skip=true'
                    sh 'mvn -f url-common clean install -Dmaven.test.skip=true'
                    for(service_name in service_names.tokenize(',')){
                        sh "mvn -f ${service_name} clean package -Dmaven.test.skip=true"
                    }
                }

            }

        }
        stage("Image build and upload"){
            steps{
                script{
                    for(service_name in service_names.tokenize(',')){
                        sh "~/docker_rm.sh ${service_name}"
                        def version = sh script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
                        sh "cd ${service_name} && docker build -t ${imageRegistry}/${projectName}-${service_name}:${version} ."
                        docker.withRegistry("https://${imageRegistry}", "${imageRegistryZone}:${registryCredential}") {
                            docker.image("${imageRegistry}/${projectName}-${service_name}:${version}").push()
                        }
                    }
                }
            }
        }

    }
}
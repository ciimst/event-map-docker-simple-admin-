pipeline{
	agent any
	    tools{
	        maven "maven 3.5.0"
	    }
	stages{
    	    stage('bash Script.sh'){
	        steps{
checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '13daaff0-994d-486d-8d1b-0f050daeeb26', url: 'https://github.com/ciimst/event-map-docker-simple-admin-.git']])		    
	        
		   sh 'bash Script.sh -r localhost:5000'
	        }
	     }

	    stage('helm install event-map-chart-admin'){
                steps{
                 script{
		   sh 'helm uninstall event-map-chart-admin'
	           sh 'helm install event-map-chart-admin ./event-map-helm-chart'
                 }
                }

    	     }

         }
  }

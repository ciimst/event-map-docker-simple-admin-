REGISRTY=docker.io/imst
TAG=1.4.6
MINIKUBE_DRIVER=docker


while getopts r:t: option
do 
    case "${option}"
        in
        r)REGISRTY=${OPTARG};;
        t)TAG=${OPTARG};;
    esac
done

# (REGISRTY ve TAG kontrol)

echo "REGISRTY : $REGISRTY"
echo "TAG   : $TAG"
echo "MINIKUBE_DRIVER = $MINIKUBE_DRIVER"




echo "BUILDING Dockerfiles"

echo "BUILDING DockerfileAdmin"
docker build -t event_map_admin:$TAG -f DockerfileAdmin  .


docker tag event_map_admin:$TAG $REGISRTY/event_map_admin:$TAG


echo "PUSHING event_map_admin to $REGISRTY"
docker push $REGISRTY/event_map_admin:$TAG

docker image rm $REGISRTY/event_map_admin:$TAG







import {latLon} from './latLon'

export class DirectionWhithWaypoints{
    username:String
    title:String
    description:String
    travelMode:String
    origin: {lat: number, lng: number}
    destination:{lat: number,lng: number}
    intermidiatePoints: {
        location:
        { 
            lat:number
            lng:number
        } 
    }
    waypoints: {
        location:
        { 
            lat:number
            lng:number
        } 
    }
    visible:boolean
}
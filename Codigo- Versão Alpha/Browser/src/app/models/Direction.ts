export class Direction{
    id:String
    username:String
    title:String
    description:String
    travelMode:String
    origin: {lat: number, lng: number}
    destination:{lat: number,lng: number}
    intermidiatePoints: {lat: number,lng: number}[]
    waypoints: [
        {
        location:
        { 
            lat:number
            lng:number
        }
    } 
    ]
    visible:boolean
    type:boolean
}

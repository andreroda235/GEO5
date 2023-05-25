import { Component, OnInit ,Input, EventEmitter, Output} from '@angular/core';
import { RequestService } from '../../../services/RequestService';
import { Direction } from '../../../models/Direction';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-direction',
  templateUrl: './direction.component.html',
  styleUrls: ['./direction.component.css']
})
export class DirectionComponent implements OnInit {
  @Input() direction : Direction
  @Output() deleteDir: EventEmitter<Direction> = new EventEmitter();
  isRequestError: boolean = false;

  constructor( private req: RequestService ) { }

  ngOnInit(): void {
  }
  setClasses(){
    let classes = {
        direction: true,
        'visible': this.direction.visible
    }
    return classes
  }
  delete(direction){
    this.deleteDir.emit(direction);
  }
  showHide(){
    this.direction.visible = !this.direction.visible;
  }

  addtoFavorites(direction:Direction){
    var dir = new Direction();
    dir.id = direction.id;
    dir.description = direction.description;
    dir.destination = direction.destination;
    dir.origin = direction.origin;
    dir.title = direction.title;
    dir.travelMode = direction.travelMode;
    dir.username = direction.username;
    
    
    if(direction.type){
      dir.intermidiatePoints = [];
      for(var i=0;i<direction.waypoints.length;i++){
        dir.intermidiatePoints[i] = direction.waypoints[i].location;
      }
    }
    
    console.log(dir);
    this.req.addToFovorites(dir).subscribe(
      (data : any)=>{
        
        console.log("Dir Added");
      },
      (err : HttpErrorResponse)=>{
        this.isRequestError = true;
        setTimeout( () => this.isRequestError = false , 2500 );     
    
  
      });
  }
}

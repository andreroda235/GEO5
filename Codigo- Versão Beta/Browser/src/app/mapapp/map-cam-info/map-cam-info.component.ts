import { Component, OnInit, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-map-cam-info',
  templateUrl: './map-cam-info.component.html',
  styleUrls: ['./map-cam-info.component.css']
})
export class MapCamInfoComponent implements OnInit {
  @Output() hideRoute: EventEmitter<boolean> = new EventEmitter();

  constructor() { }

  ngOnInit() {
		
    }

    hideRoutes($event){
      this.hideRoute.emit($event);
    }


}

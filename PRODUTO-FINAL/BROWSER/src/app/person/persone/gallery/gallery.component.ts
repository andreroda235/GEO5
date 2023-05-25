import { Component, OnInit } from '@angular/core';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';

export interface Tile {
  color: string;
}

@Component({
  selector: 'app-gallery',
  templateUrl: './gallery.component.html',
  styleUrls: ['./gallery.component.css']
})
export class GalleryComponent implements OnInit {

  tiles: Tile[] = [];

  constructor(private req: RequestService) { }

  ngOnInit(): void {
    this.tiles = [];
    
    this.req.getmyCams().subscribe(
      (data : any )=>{ 
        data.map( element => {
          
          this.req.getRoutePhotos(element.id).subscribe(
            images=>{
              images.map(
                a=>{                
                this.tiles.push(
                  {
                    color: "https://storage.cloud.google.com/apdc-geoproj.appspot.com/"+a
                  }
                );
                }
              );
            }
          );
        });
      },(err: HttpErrorResponse)=>{ console.log(err)}
    );
  }

}

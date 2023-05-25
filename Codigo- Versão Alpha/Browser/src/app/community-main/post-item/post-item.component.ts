import { Component, OnInit, Input } from '@angular/core';
import { Post } from 'src/app/models/post';


@Component({
  selector: 'app-post-item',
  templateUrl: './post-item.component.html',
  styleUrls: ['./post-item.component.css']
})
export class PostItemComponent implements OnInit {

  @Input() post: Post
  constructor() { }

  ngOnInit(): void {
  }
  
  setClasses() {
    let classes = {
      todo: true
    }

    return classes;
  }

}

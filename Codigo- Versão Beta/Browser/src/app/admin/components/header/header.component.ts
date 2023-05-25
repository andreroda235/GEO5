import { Component, OnInit, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Output() toggleSideBarForMenu: EventEmitter<any> = new EventEmitter()

  constructor() { }

  ngOnInit(): void {}

  toggleSideBar($event: any) {
    this.toggleSideBarForMenu.emit();
    // For resize if needed
    setTimeout(() => {
      window.dispatchEvent(
        new Event('resize')
      );
    }, 300)
  }

}

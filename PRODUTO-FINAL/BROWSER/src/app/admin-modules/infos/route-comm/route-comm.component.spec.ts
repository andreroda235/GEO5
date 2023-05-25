import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteCommComponent } from './route-comm.component';

describe('RouteCommComponent', () => {
  let component: RouteCommComponent;
  let fixture: ComponentFixture<RouteCommComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RouteCommComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RouteCommComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

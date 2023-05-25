import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MapappComponent } from './mapapp.component';

describe('MapappComponent', () => {
  let component: MapappComponent;
  let fixture: ComponentFixture<MapappComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MapappComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MapappComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RockTrophyComponent } from './rock-trophy.component';

describe('RockTrophyComponent', () => {
  let component: RockTrophyComponent;
  let fixture: ComponentFixture<RockTrophyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RockTrophyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RockTrophyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
